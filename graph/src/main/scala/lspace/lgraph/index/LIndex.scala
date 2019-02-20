package lspace.lgraph.index

import lspace.librarian.process.traversal.{P, UntypedTraversal}
import lspace.librarian.structure.Property
import lspace.librarian.structure.index.Index
import lspace.librarian.structure.index.shape.Shape

object LIndex {
  def apply(traversal: UntypedTraversal): LIndex = new LIndex(traversal)
}

class LIndex(val traversal: UntypedTraversal) extends Index {

  def store(shape: Shape): Unit = synchronized {}

  def find(values: Vector[Map[Property, List[P[_]]]]): List[Shape] = {
    List()
  }

  def delete(shape: Shape): Unit = {}
}