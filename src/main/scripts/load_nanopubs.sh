#------------------------------------------------------------------------------
# Loading Nanopublications to Virtuoso Triple Store
#------------------------------------------------------------------------------
# This script loads nanopublication files in TriG syntax to the Virtuoso
# triple store. You might have to adjust the DirsAllowed parameter in the
# virtuoso.ini file.
#
# Usage:
#
#     ./load_nanopubs.sh [files]
#
# Examples:
#
#     ./load_nanopubs.sh ../resources/examples/malaria.trig
#     ./load_nanopubs.sh ../resources/examples/*.trig
#
#------------------------------------------------------------------------------

VTCMD=isql
echo "Trying command '$VTCMD'"
$VTCMD --help | grep "Virtuoso" &> /dev/null

if [ $? -eq 1 ]; then
  VTCMD=isql-vt
  echo "Trying command '$VTCMD'"
  $VTCMD --help | grep "Virtuoso" &> /dev/null
fi

if [ $? -eq 1 ]; then
  echo "Error: Virtuoso not found"
  exit 1
fi

echo "Using command '$VTCMD'"

(
  for f in "$@"; do
    ls $PWD/$f
  done
) \
  | awk '{
    print "ttlp (file_to_string_output (\047"$1"\047), \047\047, \047this-graph-should-be-empty\047, 256);"
  }' \
  | $VTCMD

echo ""
