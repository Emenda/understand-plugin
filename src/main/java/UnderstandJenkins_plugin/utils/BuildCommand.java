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

package UnderstandJenkins_plugin.utils;

import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.util.ArgumentListBuilder;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This entity allow to Build all the command using during an Understand analysis.
  */
public class BuildCommand {
    
   private ArgumentListBuilder args;
   private boolean isUnix;
   private FilePath currentBuild;

    public FilePath getCurrentBuild() {
        return currentBuild;
    }

    public ArgumentListBuilder getArgs() {
        return args;
    }
    
    
    public BuildCommand(boolean isUnix,AbstractBuild<?,?> owner) throws IOException, InterruptedException {
        this.args = new ArgumentListBuilder();
        this.isUnix=isUnix;        
        //A new directory is created to save the html reports of the analyze
        FilePath fp=new FilePath(owner.getWorkspace(),"build_"+owner.getId());
        fp.mkdirs();
        currentBuild=fp;
        buildCommandInit();       
    }
    
    //Allow to build the generic command to run on Jenkins
    private void buildCommandInit()
    {
        this.args.clear();
        if(!isUnix)
        {
            this.args.add("cmd.exe","/C").add("und");
        }
        else
        {
            this.args.add("/bin/bash").add("und");
        }
        
    }
    
    //Allow to build the create command of Understand analysis.
    public ArgumentListBuilder buildCreateCommand(String dbName,String languages,AbstractBuild<?, ?> owner)
    {
       buildCommandInit();
      
      
                this.args.add("create").add("-db").add(dbName+".udb")
               .add("-languages");
       
       String[] language=languages.split(":");
       for(int i =0; i < language.length ; i++)
       {
           this.args.add(language[i]);
       }
        
         return this.args;
            
       
    }
    
    //Allow to build the add command of Understand analysis
    public ArgumentListBuilder buildAddCommand(String dbName,String pathSrcFiles)
    {
        buildCommandInit();
        this.args.add("add")
                 .add(pathSrcFiles + "/")
                 .add(dbName+".udb");
        return this.args;
    }
    
    //Allow to build the analyze command of Understand analysis
     public ArgumentListBuilder buildAnalyzeCommand(String dbName)
    {
        buildCommandInit();
        this.args
                 .add("analyze")
                 .add(dbName+".udb");
        return this.args;
    }
     
    //Allow to build the codecheck command of Understand analysis
    public ArgumentListBuilder buildCodeCheckCommand(String dbName,String configPath)
    {
        buildCommandInit();
        
        this.args.add("codecheck")
                 .add("-htmlsnippets").add(configPath)
                 .add(currentBuild.getName()+"/codecheckReport")
                 .add(dbName+".udb");
        return this.args;
    }
    
    //Allow to build the report command of Understand analysis
    public ArgumentListBuilder buildReportCommand(String dbName)
    {
        buildCommandInit();
        this.args
                .add("report")
                .add(dbName+".udb");
        return this.args;
    }

    
    //Allow to build the metrics command of Understand analysis

    public ArgumentListBuilder buildMetricsCommand(String dbName)
    {
        buildCommandInit();
        this.args
                .add("metrics")
                .add("-html")
                .add(dbName+".udb");
        return this.args;
    }
}
