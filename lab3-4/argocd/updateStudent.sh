#!/bin/zsh

#originPrefix="std-1"
originPrefix="ahsoka20"
originGitAccount=jbcodeforce

echo "Updating prefix to " $PREFIX
echo "Updating prefix to " $GIT_ACCOUNT

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
	   sed -i ''  "s/$originGitAccount/$GIT_ACCOUNT/g" $f
	else
	   sed -i  "s/$originPrefix/$PREFIX/g" $f
           sed -i  "s/$originGitAccount/$GIT_ACCOUNT/g" $f
	fi
    fi
done 
