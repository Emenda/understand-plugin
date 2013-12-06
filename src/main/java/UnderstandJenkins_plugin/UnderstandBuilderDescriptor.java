/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package UnderstandJenkins_plugin;

import hudson.Extension;
import hudson.Util;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

/**
 *
 * @author Guillaume Pouilloux
 */   
public class UnderstandBuilderDescriptor extends BuildStepDescriptor<Builder> {
    private String undPath;

    public UnderstandBuilderDescriptor() {
        super(UnderstandBuilder.class);
        load();
    }

    public boolean isApplicable(Class<? extends AbstractProject> aClass) {
        // Indicates that this builder can be used with all kinds of project types 
        return true;
    }

    @Override     
    public boolean configure(StaplerRequest req, JSONObject json) throws Descriptor.FormException {
        json = json.getJSONObject("und");
        undPath = json.getString("undPath");
        save();         
        return true;     
    }      

    public String getUndPath() {
        return undPath;
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

