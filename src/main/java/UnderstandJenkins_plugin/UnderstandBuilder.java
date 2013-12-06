/*******************************************************************************
 * Copyright (c) 2011 Emenda France Sarl                                      *
 * Author : Yoan Poigneau                                                       *
 *		                                                                        *
 * Permission is hereby granted, free of charge, to any person obtaining a copy *
 * of this software and associated documentation files (the "Software"), to deal*
 * in the Software without restriction, including without limitation the rights *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell    *
 * copies of the Software, and to permit persons to whom the Software is        *
 * furnished to do so, subject to the following conditions:                     *
 *                                                                              *
 * The above copyright notice and this permission notice shall be included in   *
 * all copies or substantial portions of the Software.                          *
 *                                                                              *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR   *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,     *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE  *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER       *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,*
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN    *
 * THE SOFTWARE.                                                                *
 *                                                                              *
 *******************************************************************************/

package UnderstandJenkins_plugin;
import UnderstandJenkins_plugin.utils.BuildCommand;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

/** 
 * Entity that allow to run an Understand analysis on a Jenkins build
 */
public class UnderstandBuilder extends Builder {

    //The name of the understand db to create on the jenkins workspace
    private String dbName;    
    // The path to the files to analyze with understand
    private String pathFiles;
    //Path to the configuration of codeCheck
    private String pathCodeCheckIni;
    //codeCheck report have to be created?
    boolean codeCheck=true;
    //metric report have to be created?
    boolean metrics=true;
    //Project report have to be created?
    boolean report=true;
    //The languages of the project to analyze
    private UnderstandLanguageContainer language;
    boolean forceCreation=false;

   
    
    @DataBoundConstructor
    public UnderstandBuilder(String dbName, String pathFiles, String pathCodeCheckIni, UnderstandLanguageContainer lang,boolean forceCreation, boolean codeCheck, boolean metrics, boolean report)
    {
        this.dbName=dbName;
        this.pathFiles=pathFiles;
        this.pathCodeCheckIni=pathCodeCheckIni;
        this.codeCheck=codeCheck;
        this.metrics=metrics;
        this.report=report;
        this.language=lang;
        this.forceCreation=forceCreation;
        
    }
    
    
    public UnderstandBuilder()
    {        
    }

    public UnderstandLanguageContainer getLanguage() {
        return language;
    }

     public boolean isForceCreation() {
        return forceCreation;
    }

    public boolean isCodeCheck() {
        return codeCheck;
    }
 
    public boolean isMetrics() {
        return metrics;
    }

    public boolean isReport() {
        return report;
    }
      public String getDbName() {
        return dbName;
    }

    public String getPathFiles() {
        return pathFiles;
    }
    

    public String getPathCodeCheckIni() {
        return pathCodeCheckIni;
    }
    
    //This method run all the Understand Command for analyze a project.
    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, IOException, InterruptedException {
      BuildCommand buildCmd= new BuildCommand(launcher.isUnix(),build);
       
        try {
           
            int join;
            
            FilePath fp=new FilePath(build.getWorkspace(),dbName+".udb");
            boolean state=fp.exists();
            if(!fp.exists()||forceCreation){  
            
                join=launcher.launch().cmds(buildCmd.buildCreateCommand(this.dbName,this.language.getLanguages(),build)).envs(build.getEnvironment(listener)).stdout(listener).pwd(build.getWorkspace()).join();
                if(join==1) {
                    throw new Exception("Error at project creation.");
                }
            }
            
            join = launcher.launch().cmds(buildCmd.buildAddCommand(this.dbName, this.pathFiles)).envs(build.getEnvironment(listener)).stdout(listener).pwd(build.getWorkspace()).join();
            if(join==1) {
                throw new Exception("Error adding sources files");
            }
            
            
            join=launcher.launch().cmds(buildCmd.buildAnalyzeCommand(this.dbName)).envs(build.getEnvironment(listener)).stdout(listener).pwd(build.getWorkspace()).join();
            if(join==1) {
                throw new Exception("Error in analyze.");
            }
            
            if(this.metrics)
            {    
                join=launcher.launch().cmds(buildCmd.buildMetricsCommand(this.dbName)).envs(build.getEnvironment(listener)).stdout(listener).pwd(build.getWorkspace()).join();
                if(join==1) {
                    throw new Exception("Error creating Metrics reports.");
                }
                FilePath source=new FilePath(build.getWorkspace(),dbName+"_Metrics");
                FilePath destination=new FilePath(build.getWorkspace(),"build_"+build.getId()+"/"+dbName+"_Metrics");
                destination.mkdirs();
                source.moveAllChildrenTo(destination);
                build.addAction(new UnderstandReport(this.dbName,build,"metrics"));
            }
            
            if(this.report)
            {
                join=launcher.launch().cmds(buildCmd.buildReportCommand(this.dbName)).envs(build.getEnvironment(listener)).stdout(listener).pwd(build.getWorkspace()).join();
                if(join==1) {
                    throw new Exception("Error creating report.");
                }
                FilePath source=new FilePath(build.getWorkspace(),dbName+"_html");
                FilePath destination=new FilePath(build.getWorkspace(),"build_"+build.getId()+"/"+dbName+"_html");
                destination.mkdirs();
                source.moveAllChildrenTo(destination);
                build.addAction(new UnderstandReport(this.dbName,build,""));
            }
            
            if(this.codeCheck)
            {
                join=launcher.launch().cmds(buildCmd.buildCodeCheckCommand(this.dbName, this.pathCodeCheckIni)).envs(build.getEnvironment(listener)).stdout(listener).pwd(build.getWorkspace()).join();
                if(join==1) {
                    throw new Exception("Error creating codeCheck reports.");
                }
                
                build.addAction(new UnderstandReport(this.dbName,build,"codeCheck"));
                
            }     
        
        } catch (Exception ex) {
            build.setResult(Result.FAILURE);
            Logger.getLogger(UnderstandBuilder.class.getName()).log(Level.SEVERE, null, ex);
            
        }        
        return true;
    }


    @Override
    public UnderstandBuilder.UndDescriptor getDescriptor() {
        return DESCRIPTOR;
    }

    
    @Extension    
    public static final UnderstandBuilder.UndDescriptor DESCRIPTOR = new UnderstandBuilder.UndDescriptor();
    public static final class UndDescriptor extends BuildStepDescriptor<Builder> {
        
        public UndDescriptor() {
            super(UnderstandBuilder.class);
            load();
        }
        
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types 
            return true;
        }

        
        public String getDisplayName() {
            return "Run an Understand Analysis";
        }
        
        public UnderstandLanguageContainer getLanguage() {
            return new UnderstandLanguageContainer();
        }  
        
        public FormValidation doCheckDbName(@QueryParameter String value) {
            String dbName = Util.fixEmptyAndTrim(value);
            if (dbName == null || dbName.isEmpty()) {
                return FormValidation.error("Database Name is mandatory");
            } else {
                return FormValidation.ok();
            }
        }
        
        public FormValidation doCheckPathFiles(@QueryParameter String value) {
            String pathFiles = Util.fixEmptyAndTrim(value);
            if (pathFiles == null || pathFiles.isEmpty()) {
                return FormValidation.error("Path to source files is mandatory");
            } else {
                return FormValidation.ok();
            }
        }
        public FormValidation doCheckPathCodeCheckIni(@QueryParameter String value) {
            String pathCodeCheckIni = Util.fixEmptyAndTrim(value);
            if (pathCodeCheckIni == null || pathCodeCheckIni.isEmpty()) {
                return FormValidation.error("Path to codeCheck configuration file is mandatory");
            } else {
                return FormValidation.ok();
            }
        }       
    }
}

