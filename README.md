 > Task :tasks

------------------------------------------------------------
All tasks runnable from root project
------------------------------------------------------------

Build tasks
-----------
*assemble* - Assembles the outputs of this project.</br>
*build* - Assembles and tests this project.</br>
*buildDependents* - Assembles and tests this project and all projects that depend on it.</br>
*buildNeeded* - Assembles and tests this project and all projects it depends on.</br>
*classes* - Assembles main classes.</br>
*clean* - Deletes the build directory.</br>
*jar* - Assembles a jar archive containing the main classes.</br>
*testClasses* - Assembles test classes.</br>

Build Setup tasks
-----------------
*init* - Initializes a new Gradle build.</br>
*wrapper* - Generates Gradle wrapper files.</br>

Documentation tasks
-------------------
*javadoc* - Generates Javadoc API documentation for the main source code.</br>

Help tasks
----------
*buildEnvironment* - Displays all buildscript dependencies declared in root project 'mntlab-pipeline'.</br>
*components* - Displays the components produced by root project 'mntlab-pipeline'. [incubating]</br>
*dependencies* - Displays all dependencies declared in root project 'mntlab-pipeline'.</br>
*dependencyInsight* - Displays the insight into a specific dependency in root project 'mntlab-pipeline'.</br>
*dependentComponents* - Displays the dependent components of components in root project 'mntlab-pipeline'. [incubating]</br>
*help* - Displays a help message.</br>
*model* - Displays the configuration model of root project 'mntlab-pipeline'. [incubating]</br>
*projects* - Displays the sub-projects of root project 'mntlab-pipeline'.</br>
*properties* - Displays the properties of root project 'mntlab-pipeline'.</br>
*tasks* - Displays the tasks runnable from root project 'mntlab-pipeline'.</br>

Verification tasks
------------------
*check* - Runs all checks.</br>
*cucumber* - Runs cucumber tests.</br>
*jacocoTestReport* - Generates code coverage report for the test task.</br>
*test* - Runs the unit tests.</br>

Rules
-----
Pattern: *clean*<TaskName>: Cleans the output files of a task.</br>
Pattern: *build*<ConfigurationName>: Assembles the artifacts of a configuration.</br>
Pattern: *upload*<ConfigurationName>: Assembles and uploads the artifacts belonging to a configuration.</br>
</br>
To see all tasks and more detail, run gradle tasks --all</br>
</br>
To see more detail about a task, run gradle help --task <task></br>
