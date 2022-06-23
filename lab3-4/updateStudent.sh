#!/bin/zsh

originPrefix="ahsoka20"
#originPrefix="cody20"
if [ -z "$PREFIX" ]
then
    echo
    echo "do the following command: export PREFIX=<allocated-userid>"
    exit
fi

echo "Updating prefix to " $PREFIX

contains() {
    string="$1"
    substring="$2"
    test -n "$string" || test -z "$substring" && test -z "${string##*"$substring"*}"
}
# Process each yamls file in the tree, but not the kustomization files
for f in $(ls -R **/*.yaml)
do 
    if contains $f  "kustomization.yaml"
    then
        echo "Skip $f"
    else
        echo "Modify $f"
	if [[ $OSTYPE == 'darwin'* ]]; then
     	   sed -i ''  "s/$originPrefix/$PREFIX/g" $f
	else
           sed -i "s/$originPrefix/$PREFIX/g" $f
	fi
    fi
done
