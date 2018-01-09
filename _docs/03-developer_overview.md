---
title: "Toolkit Development overview"
permalink: /docs/developer/overview/
excerpt: "Contributing to this toolkits development."
last_modified_at: 2017-08-04T12:37:48-04:00
redirect_from:
   - /theme-setup/
sidebar:
   nav: "developerdocs"
---
{% include toc %}
{% include editme %}

## Getting Started with Development

To get started with developing and building the sparkmllib toolkit, follow the steps below:

   1. Clone the repository to your local file system.
   2. Install Maven
   3. Once Maven is installed, export M2_HOME="Mavin_Install_Location"
   4. Go to the root of the toolkit (com.ibm.streamsx.sparkmllib)
   5. Type "ant"

This will invoke the Ant Build. build.xml will in turn invoke Maven to download all dependencies required for Spark. Once the dependencies are downloaded, build.xml will proceed to build the java files and call spl-make-toolkit.

## Getting Started with Streams Studio
Note: Make sure Ant and Maven are installed in your remote host.

To import this toolkit project into Streams Studio:

    Install "Egit" into Streams Studio (https://www.eclipse.org/egit/)
    In the Git Repositories View, click on the "Clone a Git Repitory" button
    After the repository is cloned, follow the dialog to import existing project into your workspace.
    You will not have the dependencies needed for Studio to build the project correctly. Go to the command line and to the root of the toolkit project (for example, streamsx.sparkmllib/com.ibm.streamsx.sparkmllib).
    Type "ant maven-deps" - to kick off a build at the command line for the first time. This will get all the dependencies downloaded.
    Refresh your project in Streams Studio to get all the jar files included in your workspace
    Build the com.ibm.streamsx.sparkmllib toolkit project in Studio - The build should be successful at this point.



## Getting Started with Streams Studio on Windows


Note: Make sure Ant and Maven are installed in your remote host.

You can set up remote development on Windows.

To import this toolkit project into Streams Studio on Windows:

    Install "Egit" into Streams Studio (https://www.eclipse.org/egit/)
    In the Git Repositories View, click on the "Clone a Git Repitory" button
    After the repository is cloned, follow the dialog to import existing project into your workspace.
    Select the toolkit project, Right click -> Copy project to remote host - This will copy the project to the remote Linux host where actual compilation needs to happen.
    You will not have the dependencies needed for Studio to build the project correctly at this point. Open a command prompt to the remote Linux host.
    Go to the root directory of the toolkit project
    Type "ant maven-deps" - to kick off the ant build that pulls in all the necessary dependencies.
    In Streams Studio on Windows, select the "opt" folder under the toolkit project. Right click -> Remote Reconciler -> Pull from (remote host) - This action will pull all the jar files from your remote Linux host to your Windows machine.
    Build the com.ibm.streamsx.sparkmllib toolkit project in Studio - The build should be successful at this point.

