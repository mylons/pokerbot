package org.poker.irc.messagehandler;

import com.google.common.base.Strings;
import it.jtomato.JTomato;
import it.jtomato.gson.Movie;
import org.pircbotx.hooks.events.MessageEvent;
import org.poker.irc.MessageEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RottenTomatoesMessageEventHandler implements MessageEventHandler {
  private static final Logger LOG = LoggerFactory.getLogger(RottenTomatoesMessageEventHandler.class);

  @Override
  public String[] getMessagePrefixes() {
    return new String[] {".rt", "!rt"};
  }

  @Override
  public String getDescription() {
    return "!rt <title> or .rt <title>: send to channel rotten tomatoes critic rating, audience rating, and URL for <title>";
  }

  @Override
  public void onMessage(MessageEvent event) {
    String message = event.getMessage();
    String movieName;
    if (message.startsWith(".rt")) {
      movieName = message.substring(".rt".length()).trim();
    } else {
      movieName = message.substring("!rt".length()).trim();
    }
    Map<String, String> env = System.getenv();
    String API_KEY = env.get("RT_API_KEY");
    if (Strings.isNullOrEmpty(API_KEY)) {
      event.getChannel().send().message("Can't RottenTomato: set the RT_API_KEY environment variable");
    } else {
      JTomato jTomato = new JTomato(API_KEY);
      List<Movie> movies = new ArrayList<Movie>();
      int total = jTomato.searchMovie(movieName, movies, 1);
      if (total == 0) {
        event.getChannel().send().message("RottenTomatoes - '" + movieName + "' not found. Incorrect movie name?");
      } else {
        Movie movie = movies.get(0);
        StringBuilder sb = new StringBuilder();
        sb.append("RottenTomatoes - ");
        sb.append(movie.title);
        sb.append(" | ");
        sb.append(" critics: ");
        sb.append(movie.rating.criticsScore);
        sb.append("% | audience: ");
        sb.append(movie.rating.audienceScore);
        sb.append("% | ");
        sb.append(movie.links.alternate);
        event.getChannel().send().message(sb.toString());
      }
    }
  }
}
