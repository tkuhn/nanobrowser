nanobrowser
===========

A browser for nanopublications. Under construction...

See the [public demo](http://nanobrowser.inn.ac/).

The code is available under the LGPL license. See LICENSE.txt for the details.


Publication
-----------

Nanobrowser is described in the following publication:

> Tobias Kuhn, Paolo Emilio Barbano, Mate Levente Nagy, and Michael
> Krauthammer. Broadening the Scope of Nanopublications. In Proceedings of the
> 10th Extended Semantic Web Conference (ESWC). 2013.
> http://www.tkuhn.ch/pub/kuhn2013eswc.pdf


Screenshots
-----------

Some screenshots of the browser interface:

![Screenshot of sentence page](screenshot1.png)

![Screenshot of nanopub page](screenshot2.png)


Virtuoso
--------

Nanobrowser uses the Virtuoso triple store via its SPARQL endpoint. You should
use a separate Virtuoso instance for nanobrowser. Make sure that the SPARQL
endpoint is accessible to nanobrowser but not directly to web users.

The following commands have to be executed to configure the Virtuoso triple
store:

    GRANT EXECUTE ON DB.DBA.RDF_QUAD TO "SPARQL";
    GRANT DELETE ON DB.DBA.RDF_QUAD TO "SPARQL";
    GRANT EXECUTE ON DB.DBA.SPARUL_CLEAR TO "SPARQL";
    GRANT EXECUTE ON DB.DBA.SPARQL_INSERT_DICT_CONTENT TO "SPARQL";
    GRANT EXECUTE ON DB.DBA.SPARQL_DELETE_DICT_CONTENT TO "SPARQL";

To allow for text indexing (not yet implemented):

    RDF_OBJ_FT_RULE_ADD(null, null, 'All');
    VT_INC_INDEX_DB_DBA_RDF_OBJ();
