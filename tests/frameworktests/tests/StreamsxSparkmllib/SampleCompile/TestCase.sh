#--variantList=$(\
#--for x in $TTPR_SreamsxSparkmllibSamplesPath/*; \
#--	do if [[ -f $x/Makefile ]]; then \
#--		echo -n "${x#$TTPR_SreamsxSparkmllibSamplesPath/} "; \
#--	fi; \
#--	done\
#--)

setCategory 'quick'

function testStep {
	local save="$PWD"
	cd "$TTPR_SreamsxSparkmllibSamplesPath/$TTRO_variantCase"
	pwd
	export SPL_CMD_ARGS="-j $TTRO_treads"
	export STREAMS_SPARKMLLIB_TOOLKIT="$TTPR_streamsxSparkmllibToolkit"
	echoExecuteInterceptAndSuccess 'make' 'all'
	cd "$save"
	return 0
}