Assignment #4

From student_package/:
  javac src/*.java tests/GraphSmokeTests.java
  java -cp src CampusNav data/ut_austin_campus_100plus.txt
  java -ea -cp src:tests GraphSmokeTests

On Windows replace classpath colon with semicolon.
The starter implementation compiles but intentionally throws UnsupportedOperationException.
The tests fail until you implement the methods.
Input lines contain undirected edges; # lines and blank lines are ignored.
Building codes are case-sensitive.
