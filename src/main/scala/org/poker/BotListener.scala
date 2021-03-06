package org.poker

import com.typesafe.scalalogging.slf4j.LazyLogging
import org.poker.handler.MessageEventHandler
import org.pircbotx.hooks.events.MessageEvent
import org.pircbotx.hooks.ListenerAdapter
import org.pircbotx.PircBotX

class BotListener() extends ListenerAdapter[PircBotX] with LazyLogging {
  var handlers = List[MessageEventHandler]()

  def addHandler(handler: MessageEventHandler): Unit = {
    handlers = handler::handlers
  }

  override def onMessage(event: MessageEvent[PircBotX]): Unit = {
    for (h <- handlers) {
      val m = h.messageMatchRegex.findFirstMatchIn(event.getMessage)
      if (m.isDefined) {
        try {
          h.onMessage(event, m.get)
        } catch {
          case t: Throwable => logger.warn("Error executing org.poker.handler", t)
        }
      }
    }
  }
}
