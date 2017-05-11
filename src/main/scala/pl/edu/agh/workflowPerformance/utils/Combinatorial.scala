package pl.edu.agh.workflowPerformance.utils

/**
  * @author lewap
  * @since 04.12.16
  */
trait Combinatorial {

  def subsetsFromList[T](list: List[T], subsetMember: List[T] = List.empty[T]): List[List[T]] = {
    if (list.isEmpty) {
      subsetMember :: Nil
    } else {
      val newSubsetMember = list.head :: subsetMember
      subsetsFromList[T](list.tail, subsetMember) ::: subsetsFromList(list.tail, newSubsetMember)
    }
  }

  def subsetsFromMap[K, V](map: Map[K, V], subsetMember: Map[K, V] = Map.empty[K, V]): List[Map[K, V]] = {
    if (map.isEmpty) {
      subsetMember :: Nil
    } else {
      val newSubsetMember = subsetMember + map.head
      subsetsFromMap(map.tail, subsetMember) ::: subsetsFromMap(map.tail, newSubsetMember)
    }
  }

}
