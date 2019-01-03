package lspace.librarian.process.traversal.p

import lspace.librarian.process.traversal.P.EqHelper
import lspace.librarian.process.traversal.helper.ClassTypeable
import lspace.librarian.process.traversal.{EqP, P, PredicateDef, PredicateWrapper}
import lspace.librarian.provider.detached.DetachedGraph
import lspace.librarian.provider.mem.MemGraphDefault
import lspace.librarian.provider.mem.MemGraphDefault
import lspace.librarian.provider.wrapped.WrappedNode
import lspace.librarian.structure._

object Neqv extends PredicateDef("Neqv", `@extends` = () => List(EqP.ontology)) with PredicateWrapper[Neqv[_]] {

  def wrap(node: Node): Neqv[_] = node match {
    case node: Neqv[_] => node
    case _ =>
      val (pvalue, helper) = EqHelper map node.out(EqP.keys.value).head
      new Neqv(pvalue, node)(helper)
  }

  object keys extends EqP.Properties
  override lazy val properties: List[Property] = EqP.properties
  trait Properties extends EqP.Properties

  def apply[T: EqHelper, T0, TT0 <: ClassType[_]](pvalue: T)(implicit ct: ClassTypeable.Aux[T, T0, TT0]): Neqv[T] = {
    val node = DetachedGraph.nodes.create(ontology)

    node.addOut(EqP.keys.value, pvalue)
    new Neqv(pvalue, node)
  }
}

class Neqv[T] private (val pvalue: T, override val value: Node)(implicit helper: EqHelper[T])
    extends WrappedNode(value)
    with EqP[T] {
  def assert(avalue: Any): Boolean = helper.neqv(avalue, pvalue)

  override def prettyPrint: String = s"neqv($pvalue)"
  //  {
  //    pvalue match {
  //      case pvalue: Resource[_] => avalue match {
  //        case avalue: Resource[_] => pvalue.iri != avalue.iri
  //        case _ => true
  //      }
  //      case _ => pvalue != avalue
  //    }
  //  }
}