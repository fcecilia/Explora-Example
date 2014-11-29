package example.movies

import com.explora.ld.dbpedia.{DBPedia, DBTrait, LiveDBPedia}
import com.explora.model.Entity
import com.explora.pattern.ExploraHelper._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/**
 * Created by fred on 08/11/2014.
 */


object Main extends App {

  val film = "Full_Metal_Jacket"

  for {
    dbpedia <- MoviesRecoDBPedia.start(film)
    dbpediaLive <- MoviesRecoLiveDBPedia.start(film)
  } yield {

    println("--------------------------------------------------------------------------------")
  }
}

object MoviesRecoDBPedia extends RecoEngine with DBPedia {

  def start(film: String) = recommand(DBEntity(film))

}

object MoviesRecoLiveDBPedia extends RecoEngine with LiveDBPedia {

  def start(film: String) = recommand(DBEntity(film))

}


trait RecoEngine {

  self: DBTrait =>

  def recommand(film: Entity): Future[Unit] = {
    val smillis = System.currentTimeMillis()

    println(repository)
    println(wikiPage(film))


    val requestF = s"""
  select ?movies  where {
    {
      <$film> <http://dbpedia.org/ontology/starring> ?starring.
      ?movies <http://dbpedia.org/ontology/starring> ?starring.
    } UNION {
      <$film> <http://dbpedia.org/ontology/director> ?director.
      ?movies <http://dbpedia.org/ontology/director> ?director.
    } UNION {
      <$film> <http://purl.org/dc/terms/subject> ?subject.
       ?movies <http://purl.org/dc/terms/subject> ?subject.
    }
   } """ executeAndGet "movies"

    requestF.map { nodes =>

      val movies: List[String] = nodes.onlyEntities.filter(n => n != film).map(_.uri)

      val by: List[(String, Int)] = movies.groupBy(a => a).map(m => (m._1, m._2.size)).toList.sortBy(-_._2).take(7)

      println(System.currentTimeMillis() - smillis)

      by.map(t =>wikiPage(t._1) +": "+t._2).foreach(println)
      println("\n")


    }
  }
}
