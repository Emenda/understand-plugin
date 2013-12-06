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
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.model.Result;
import hudson.tasks.Builder;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kohsuke.stapler.DataBoundConstructor;

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
    private String listFile;
   
    
    @DataBoundConstructor
    public UnderstandBuilder(String dbName, String pathFiles, String pathCodeCheckIni, UnderstandLanguageContainer lang,boolean forceCreation, boolean codeCheck, boolean metrics, boolean report, String pathListFiles)
    {
        this.dbName=dbName;
        this.pathFiles=pathFiles;
        this.pathCodeCheckIni=pathCodeCheckIni;
        this.codeCheck=codeCheck;
        this.metrics=metrics;
        this.report=report;
        this.language=lang;
        this.forceCreation=forceCreation;
        this.listFile=pathListFiles;
    }
    
    
    public UnderstandBuilder()
    { }

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
    
    public String getPathListFiles() {
        return listFile;
    }
    
    //This method run all the Understand Command for analyze a project.
    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, IOException, InterruptedException {
      BuildCommand buildCmd= new BuildCommand(DESCRIPTOR.getUndPath(), launcher.isUnix(),build);
       
        try {
           
            int join;
            
            FilePath fp=new FilePath(build.getWorkspace(),dbName+".udb");
            if(!fp.exists()||forceCreation){  
            
                join=launcher.launch().cmds(buildCmd.buildCreateCommand(DESCRIPTOR.getUndPath(), this.dbName,this.language.getLanguages(),build)).envs(build.getEnvironment(listener)).stdout(listener).pwd(build.getWorkspace()).join();
                if(join==1) {
                    throw new Exception("Error at project creation.");
                }
            }
            
            join = launcher.launch().cmds(buildCmd.buildAddCommand(DESCRIPTOR.getUndPath(), this.dbName, this.pathFiles)).envs(build.getEnvironment(listener)).stdout(listener).pwd(build.getWorkspace()).join();
            if(join==1) {
                throw new Exception("Error adding sources files");
            }
            
            
            join=launcher.launch().cmds(buildCmd.buildAnalyzeCommand(DESCRIPTOR.getUndPath(), this.dbName, this.listFile)).envs(build.getEnvironment(listener)).stdout(listener).pwd(build.getWorkspace()).join();
            if(join==1) {
                throw new Exception("Error in analyze.");
            }
            
            if(this.metrics)
            {    
                join=launcher.launch().cmds(buildCmd.buildMetricsCommand(DESCRIPTOR.getUndPath(), this.dbName)).envs(build.getEnvironment(listener)).stdout(listener).pwd(build.getWorkspace()).join();
                if(join==1) {
                    throw new Exception("Error creating Metrics reports.");
                }
                FilePath source=new FilePath(build.getWorkspace(),dbName+"_Metrics");
                FilePath destination=new FilePath(build.getWorkspace(),"build_"+build.getId()+"/"+dbName+"_Metrics");
                destination.mkdirs();
                source.moveAllChildrenTo(destination);
                build.addAction(new UnderstandReport(this.dbName,build,"Metrics"));
            }
            
            if(this.report)
            {
                join=launcher.launch().cmds(buildCmd.buildReportCommand(DESCRIPTOR.getUndPath(), this.dbName)).envs(build.getEnvironment(listener)).stdout(listener).pwd(build.getWorkspace()).join();
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
                join=launcher.launch().cmds(buildCmd.buildCodeCheckCommand(DESCRIPTOR.getUndPath(), this.dbName, this.pathCodeCheckIni, this.listFile)).envs(build.getEnvironment(listener)).stdout(listener).pwd(build.getWorkspace()).join();
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
    public Descriptor<Builder> getDescriptor() {
        return DESCRIPTOR;
    }
    
    @Extension
    public static final UnderstandBuilderDescriptor DESCRIPTOR = new UnderstandBuilderDescriptor();
    
}

