#--variantCount=2

function myExplain {
	case "$TTRO_variantCase" in
	0) echo "variant $TTRO_variantCase - type of attribute analysisResult";;
	1) echo "variant $TTRO_variantCase - no attribute analysisResult on output port";;
	*) printErrorAndExit "invalid variant $TTRO_variantCase";;
	esac
}

PREPS=(
	'myExplain'
	'copyAndMorphSpl'
)

STEPS=(
	"splCompileInterceptAndError"
	'linewisePatternMatchInterceptAndSuccess "$TT_evaluationFile" "" "${errorCodes[$TTRO_variantCase]}"'
)

#	'*ERROR: CDIST2702E Expected type of attribute analysisResult is int32 but the type RSTRING:rstring was used*'
#	'*ERROR: CDIST2700E The operator requires an attribute called analysisResult on the output port*'

errorCodes=(
	'*CDIST2702E*'
	'*CDIST2700E*'
)