package lspace.lgraph.provider.cassandra

import lspace.lgraph.LGraph
import lspace.lgraph.provider.elasticsearch.ESIndexProvider
import lspace.librarian.structure.{AsyncGraphSpec, Graph}

class AsyncCassandraStoreManagerSpec extends AsyncGraphSpec {

  val store = LCassandraStoreProvider("AsyncCassandraStoreManagerSpec", "localhost", 9042)
  store.deleteAll()
  val sampleStore = LCassandraStoreProvider("AsyncCassandraStoreManagerSpec-sample", "localhost", 9042)
  sampleStore.deleteAll()

  val graph: LGraph =
    LGraph(store, new ESIndexProvider)
  val sampleGraph: LGraph =
    LGraph(sampleStore, new ESIndexProvider)
  def createGraph(iri: String): Graph = {
    val storage = LCassandraStoreProvider(iri, "localhost", 9042)
    storage.deleteAll()
    LGraph(storage, new ESIndexProvider)
  }

  override def beforeAll: Unit = {
    super.beforeAll
  }

  override def afterAll(): Unit = {
    super.afterAll()
  }

}