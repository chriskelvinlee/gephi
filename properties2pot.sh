#!/bin/bash
ROOT=`pwd`

function RecurseDirs
{
oldIFS=$IFS
IFS=$'\n'
for f in "$@"
do
if [[ $f == 'Bundle.properties' ]]; then
	PWD=`pwd`

	path=`echo "$PWD" | sed 's,.*\/src\/\(.*\)$,\1,' | sed 's,/,-,g'`

	#rm *.pot

	if [[ $path == org-* ]]; then
		echo $path
		fname=${path}.pot
		# generate POT file from Bundle.properties
		msgcat $f --properties-input --output-file=$fname

		if [[ -f $fname ]]; then
			#add header
			cp $fname tmp.txt
			cat ${ROOT}/pot-header.txt tmp.txt > $fname
			rm tmp.txt

			#check file
			msgfmt -c $fname
			rm messages.mo
		fi
	fi

fi
if [[ -d "${f}" ]]; then
	cd "${f}"
	RecurseDirs $(ls -1 ".")
	cd ..
fi
done
IFS=$oldIFS
}

RecurseDirs .

