package lspace.client

import lspace.client.session.ClientSession
import lspace.datatype.DataType
import lspace.provider.detached.DetachedGraph
import lspace.provider.wrapped.WrappedNode
import lspace.structure._
import lspace.structure.Property.default._
import lspace.structure.OntologyDef
import monix.eval.Task

object Client extends OntologyDef(lspace.NS.vocab.Lspace + "Client", Set(), "Client", "A client ..") {

  def apply(iri: String, role: Set[Role], manager: Set[User], session: Set[ClientSession]): Task[Client] = {
    for {
      node <- DetachedGraph.nodes.create(ontology)
      _    <- node.addOut(typed.iriUrlString, iri)
      _    <- Task.gatherUnordered(role.map(role => node.addOut(keys.`lspace:Client/role@Role`, role)))
      _    <- Task.gatherUnordered(manager.map(manager => node.addOut(keys.`lspace:Client/manager@User`, manager)))
      _ <- Task.gatherUnordered(
        session.map(session => node.addOut(keys.`lspace:Client/session@ClientSession`, session)))
    } yield new Client(node)
  }

  def wrap(node: Node): Client = node match {
    case node: Client => node
    case _            => Client(node)
  }

  object keys {
    object `lspace:Client/role`
        extends PropertyDef(
          lspace.NS.vocab.Lspace + "Role",
          "Role",
          "A role assigned to this user",
          `@range` = () => Role.ontology :: Nil
        ) {}
    lazy val `lspace:Client/role@Role`: TypedProperty[Node] = `lspace:Client/role` + Role.ontology

    object `lspace:Client/manager`
        extends PropertyDef(
          lspace.NS.vocab.Lspace + "Manager",
          "Manager",
          "A user who can establish or revoke the sessions of this user.",
          `@range` = () => User.ontology :: Nil
        ) {}
    lazy val `lspace:Client/manager@User`: TypedProperty[Node] = `lspace:Client/manager` + User.ontology

    object `lspace:Client/session`
        extends PropertyDef(
          lspace.NS.vocab.Lspace + "Client/session",
          "session",
          "A session ...",
          `@range` = () => DataType.default.`@datetime` :: Nil
        ) {}
    lazy val `lspace:Client/session@ClientSession`
      : TypedProperty[Node] = `lspace:Client/session` + ClientSession.ontology
  }
  override lazy val properties
    : List[Property] = keys.`lspace:Client/role`.property :: keys.`lspace:Client/manager`.property :: keys.`lspace:Client/session`.property :: Nil
  trait Properties {
    val `lspace:Client/role`: Property        = keys.`lspace:Client/role`
    val `lspace:Client/role@Role`             = keys.`lspace:Client/role@Role`
    val `lspace:Client/manager`: Property     = keys.`lspace:Client/manager`
    val `lspace:Client/manager@User`          = keys.`lspace:Client/manager@User`
    val `lspace:Client/session`: Property     = keys.`lspace:Client/session`
    val `lspace:Client/session@ClientSession` = keys.`lspace:Client/session@ClientSession`
  }
}

case class Client private (node: Node) extends WrappedNode(node) {
  def role: Set[Role]    = this.out(Client.keys.`lspace:Client/role@Role`).map(Role.wrap).toSet
  def manager: Set[User] = this.out(Client.keys.`lspace:Client/manager@User`).map(User.wrap).toSet
  def session: Set[ClientSession] =
    this.out(Client.keys.`lspace:Client/session@ClientSession`).map(ClientSession.wrap).toSet
}