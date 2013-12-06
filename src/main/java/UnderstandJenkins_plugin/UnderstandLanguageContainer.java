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

import org.kohsuke.stapler.DataBoundConstructor;

/**
 *  This entity is a storage class of all the languages of the project to analyze.
 */
public class UnderstandLanguageContainer {
   /*The languages availables*/
   private boolean java=false;
   private boolean cpp=true;
   private boolean csharp=false;
   private boolean web=false;
   private boolean ada=false;
   private boolean jovial=false;
   private boolean pascal=false;
   private boolean vhdl=false;
   private boolean python=false;
   private boolean cobol=false;
   private boolean plm=false;
   private boolean fortran=false;
  
   

    @DataBoundConstructor
    public UnderstandLanguageContainer(boolean java, boolean cpp,
            boolean csharp,boolean web, boolean ada, boolean jovial, 
            boolean pascal, boolean vhdl,boolean python,boolean cobol,
            boolean plm, boolean fortran) 
    {    
        this.java=java;
        this.cpp=cpp;
        this.csharp=csharp;
        this.web=web;
        this.ada=ada;
        this.jovial=jovial;
        this.pascal=pascal;
        this.vhdl=vhdl;
        this.python=python;
        this.cobol=cobol;
        this.plm=plm;
        this.fortran=fortran;
    }
    
    
    public UnderstandLanguageContainer()
    {}
    
    //Check the language selected by the user and give a string in format "lang1:lang2:lang3:".
    public String getLanguages()
    {
        StringBuilder sb= new StringBuilder();
        
        if(java) {
            sb.append("java").append(":");
        }
        if(cpp) {
            sb.append("c++").append(":");
        }
        if(csharp) {
            sb.append("c#").append(":");
        }
        if(web) {
            sb.append("web").append(":");
        }
        if(ada) {
            sb.append("ada").append(":");
        }
        if(jovial) {
            sb.append("jovial").append(":");
        }
        if(pascal) {
            sb.append("pascal").append(":");
        }
        if(vhdl) {
            sb.append("vhdl").append(":");
        }
        if(python) {
            sb.append("python").append(":");
        }
        if(cobol) {
            sb.append("cobol").append(":");
        }
        if(plm) {
            sb.append("plm").append(":");
        }
        if(fortran) {
            sb.append("fortran").append(":");
        }
        
        return sb.toString();
    }

    public boolean isJava() {
        return java;
    }

   public boolean isCpp() {
        return cpp;
    }

   public boolean isCsharp() {
        return csharp;
    }

   public boolean isWeb() {
        return web;
    }

   public boolean isAda() {
        return ada;
    }

    public boolean isJovial() {
        return jovial;
    }

    public boolean isPascal() {
        return pascal;
    }

   public boolean isVhdl() {
        return vhdl;
    }

    public boolean isPython() {
        return python;
    }

    public boolean isCobol() {
        return cobol;
    }

    public boolean isPlm() {
        return plm;
    }

    public boolean isFortran() {
        return fortran;
    }
}
