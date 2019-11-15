#--variantList='InvalidAttributeError  InvalidAttributeTypeError  InvalidControlPortTypeError  InvalidFloatAttributeTypeError  InvalidTestAttrError'

setCategory 'quick'

STEPS=(
	'cp "$TTRO_inputDirCase/${TTRO_variantCase}.spl" "$TTRO_workDirCase"'
	'TT_mainComposite="${TTRO_variantCase}"'
	"splCompileInterceptAndError"
	'linewisePatternMatchInterceptAndSuccess "$TT_evaluationFile" "" "${errorCodes[$TTRO_variantCase]}"'
)

declare -A errorCodes=(
	[InvalidAttributeError]='*ERROR: CDIST2700E The operator requires an attribute called analysisResult on the output port*'
	[InvalidAttributeTypeError]='*ERROR: CDIST2702E Expected type of attribute analysisResult is int32 but the type RSTRING:rstring was used*'
	[InvalidControlPortTypeError]='*ERROR: CDIST2701E The control port must have an attribute of type rstring, found FLOAT64:float64*'
	[InvalidFloatAttributeTypeError]='*ERROR: CDIST2702E Expected type of attribute analysisResult is float64 but the type INT32:int32 was used*'
	[InvalidTestAttrError]='*CDISP0048E ERROR: The testDataAttr operator parameter requires values of the list<float64> type, but values of the int32 type are specified*'
)