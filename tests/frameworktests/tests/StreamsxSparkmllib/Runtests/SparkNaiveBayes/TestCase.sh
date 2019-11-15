setCategory 'quick'

PREPS=(
	'copyOnly'
	'TT_mainComposite="test::Main"'
	'splCompile'
	'TT_sabFile="output/test.Main.sab"'
	'TT_traceLevel="error"'
	'rm -f "data/output.txt"'
)

# use TTRO_inputDirCase as model dir has hidden files and hidden files are not copied from copyOnly
STEPS=(
	'submitJob -P modelPath=$TTRO_inputDirCase/data/naivebayes_model'
	'checkJobNo'
	'waitForJobHealth'
	'waitForFinAndCheckHealth'
	'cancelJobAndLog'
	'echoExecuteInterceptAndSuccess diff data/expected.txt data/output.txt'
	'checkLogsNoError2'
)

FINS='cancelJobAndLog'
