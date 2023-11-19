package xterminators.spellingbee.cli;

import java.util.ArrayList;
import java.util.List;

import org.jline.reader.Candidate;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.reader.Completer;
import org.jline.utils.AttributedString;

public class CLITabCompleter implements Completer {

    @Override
    public void complete(LineReader reader, ParsedLine partialCommand, List<Candidate> candidate){
        List<String> availableCommands = List.of("exit", "found_words", "guess",
        "help", "load", "new", "rank", "save", "show", "shuffle", "hint");
        

        List<String> matchingCommands = new ArrayList<>();

        for(String command : availableCommands){
            if(command.startsWith(partialCommand.toString())){
                matchingCommands.add(partialCommand.toString());
            }
        }

        if(matchingCommands.size() == 1){
            candidate.add(new Candidate(AttributedString.stripAnsi(matchingCommands.get(0)), matchingCommands.get(0), null, null, null, null, true));
        }


    }

    

    
    
}
