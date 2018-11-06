package lspace.client

import lspace.librarian.provider.detached.DetachedGraph
import lspace.librarian.provider.mem.MemGraphDefault
import lspace.librarian.provider.wrapped.WrappedNode
import lspace.librarian.structure._

object Role {
  protected val ontologyNode = MemGraphDefault.ns.upsertNode(s"https://data.l-space.eu/schema/Role")
  ontologyNode.addLabel(Ontology.ontology)
  ontologyNode --- Property.default.label --> "Role" --- Property.default.language --> "en"
  ontologyNode --- Property.default.comment --> "A role ..." --- Property.default.language --> "en"
  lazy val ontology: Ontology = Ontology(ontologyNode)

  //TODO: test implicit graph helper-functions
  implicit class WithGraph(graph: Graph) {
    def newRole(iri: String): Role = {
      val node = graph.createNode(ontology)
      node.addOut(Property.default.typed.iriUrlString, iri)
      new Role(node)
    }
  }
  def apply(iri: String): Role = {
    val node = DetachedGraph.createNode(ontology)
    node.addOut(Property.default.typed.iriUrlString, iri)
    new Role(node)
  }
  def wrap(node: Node): Role = node match {
    case node: Role => node
    case _          => Role(node)
  }

  object keys {
    //    val role = PropertyKey(s"$uri/role")
    //    role.setContainer(types.set)
    //    val roleRole: TypedPropertyKey[Node] = role.addRange(Role.ontology)
    //    role.property(PropertyKeysBootstrap.labelString, "Role").head.property(PropertyKeysBootstrap.languageString, "en")
    //    role.property(PropertyKeysBootstrap.commentString, "A role assigned to this user").head
    //      .property(PropertyKeysBootstrap.languageString, "en")
  }

  //  ontology.addPropertyKey(keys.role)

  //  MemGraphDefault.ns.storeOntology(ontology)
}

case class Role private (override val value: Node) extends WrappedNode(value) {}
