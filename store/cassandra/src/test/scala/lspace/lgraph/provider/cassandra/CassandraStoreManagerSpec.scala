package lspace.lgraph.provider.cassandra

import lspace.lgraph.LGraph
import lspace.lgraph.provider.mem.MemIndexProvider
import lspace.librarian.process.computer.TaskSpec
import lspace.structure.{Graph, GraphSpec, NodeSpec}
import lspace.structure.Property.default._

class CassandraStoreManagerSpec extends GraphSpec with NodeSpec with TaskSpec {

  val store = LCassandraStoreProvider("CassandraStorageManagerSpec", "localhost", 9042)
  store.deleteAll()
  val sampleStore = LCassandraStoreProvider("CassandraStorageManagerSpec-sample", "localhost", 9042)
  sampleStore.deleteAll()

  val graph: LGraph =
    LGraph(store, new MemIndexProvider)
  val sampleGraph: LGraph =
    LGraph(sampleStore, new MemIndexProvider)
  def createGraph(iri: String): Graph = {
    val storage = LCassandraStoreProvider(iri, "localhost", 9042)
    storage.deleteAll()
    LGraph(storage, new MemIndexProvider)
  }

  override def beforeAll: Unit = {
    super.beforeAll
  }

  override def afterAll(): Unit = {
    super.afterAll()
  }

  "CassandraStoreManagerSpec" should {
    "get 10,000 times from store" ignore {
      val start = java.time.Instant.now().toEpochMilli
      val id    = sampleGraph.nodes.hasIri("place-san_jose_de_maipo").head.id
      (1 to 10000).foreach(_ => sampleGraph.nodes.hasId(id))
      val end      = java.time.Instant.now().toEpochMilli
      val duration = end - start
      scribe.info(s"get 10,000 times from store took ${duration} milli-seconds")
    }
    "create 10,000 nodes with an iri" ignore {
      val start       = java.time.Instant.now().toEpochMilli
      val transaction = graph.transaction
      (1 to 10000).foreach { i =>
        val node = transaction.nodes.create()
        node --- `@id` --> s"some-iri-10,000-$i"
      }
      transaction
        .commit()

      val end      = java.time.Instant.now().toEpochMilli
      val duration = end - start
      scribe.info(s"create 10,000 nodes took ${duration} milli-seconds")
    }
    "create 20,000 nodes with an iri" ignore {
      val start       = java.time.Instant.now().toEpochMilli
      val transaction = graph.transaction
      (1 to 20000).foreach { i =>
        val node = transaction.nodes.create()
        node --- `@id` --> s"some-iri-20,000-$i"
      }
      transaction
        .commit()

      val end      = java.time.Instant.now().toEpochMilli
      val duration = end - start
      scribe.info(s"create 20,000 nodes took ${duration} milli-seconds")
    }
  }
}
