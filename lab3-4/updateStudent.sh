#!/bin/zsh
originPrefix="std-1"
#originPrefix="poe1"
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
        sed -i "s/$originPrefix/$PREFIX/g" $f
    fi
done
