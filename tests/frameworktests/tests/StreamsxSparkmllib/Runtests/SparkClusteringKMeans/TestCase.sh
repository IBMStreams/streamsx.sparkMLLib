setCategory 'quick'

PREPS=(
	'copyOnly'
	'TT_mainComposite="test::Main"'
	'splCompile'
	'TT_sabFile="output/test.Main.sab"'
	'TT_traceLevel="warn"'
	'rm -f "data/output.txt"'
)

# use TTRO_inputDirCase as model dir has hidden files and hidden files are not copied from copyOnly
STEPS=(
	'submitJob -P modelPath=$TTRO_inputDirCase/data/kmeans_model'
	'checkJobNo'
	'waitForJobHealth'
	'TT_waitForFileName="$TT_dataDir/WindowMarker"'
	'waitForFinAndCheckHealth'
	'sleep 10'
	'cancelJobAndLog'
	'echoExecuteInterceptAndSuccess diff data/expected.txt data/output.txt'
	'checkLogsNoError2'
)

FINS='cancelJobAndLog'
