PREPS=(
	'echo "ERROR: CDIST2700E The operator requires an attribute called analysisResult on the output port"'
	'copyOnly'
)

STEPS=(
	"splCompileInterceptAndError"
	'linewisePatternMatchInterceptAndSuccess "$TT_evaluationFile" "" "*CDIST2700E*"'
)


