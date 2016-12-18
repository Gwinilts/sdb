#!/bin/bash

rm build.jar &> /dev/null
rm -r build/* &> /dev/null

if [[ -z "$@" ]]; then
	main=$(cat ./.mainclass)
fi

echo Compiling...

jfiles=$(find src -name "*.java")
javac -d build $jfiles

if [[ $? -ne 0 ]]; then
	exit 1
fi

cd build
jfiles=$(find . -name "*.class")
if [[ -z "$main" ]]; then
	echo "Please select the main class..."
	for i in $jfiles;
	do
		e=${i%.class}
		e=${e/.\// }
		e=${e/\//\.}
		echo      $e
	done;
	read main
	echo $main > ../.mainclass
fi

echo Generating jar \(build.jar\)

jar cfe ../build.jar $main $jfiles
cd ..
echo Gonna start main class: $main
echo
echo -------BEGIN JAVA OUTPUT-------
java -jar build.jar
echo --------END JAVA OUTPUT--------
