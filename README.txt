============================ Understand Jenkins Plugin ==========================

/*******************************************************************************
 * Copyright (c) 2013 EMENDA SARL								                *
 * Author : Yoan POIGNEAU                                                 		*
 *                                                                              *
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
 *******************************************************************************/

 
=============================== Installation ====================================

To install the plugin navigate to "Manage Hudson/Jenkins"->"Manage Plugins". 
Select the "Advanced" tab and under the "Upload Plugin" section press the "Browse..." button. 

Select the HPI file and follow up by pressing the "Upload" button. 

Now locate the "Installed" tab and hit "Restart Once No Jobs Are Running".

================================== Building ======================================

Using an existing or new job, go to the job's Configuration page. 
Under the Build section select to "Run Understand Analysis". 
Add a name for the project, the path to sources files to analyze, the path of the codecheck Configuration. 

Check the languages of your project and choose the reports you want to create.


Note: For an effective use of this plugin the PATH global variable has to contain the path to your Understand Installation.
