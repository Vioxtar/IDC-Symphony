package idc.symphony.music.melodygen;

import java.util.ArrayList;

public class PathStyle {

    private final static String SEPARATOR = ",";
    private final static int DEFAULT_ARG = 0;


    /**
     * Argument string parser
     * @param argsString
     * @param expectedArgs
     * @return
     */
    private static int[] parseArgs(String argsString, int expectedArgs) {
        argsString = argsString.replaceAll(" ", "");
        int[] parsedArgs = new int[expectedArgs];
        String[] separatedArgStrings = argsString.split(SEPARATOR);
        for (int i = 0; i < expectedArgs; i++) {
            int newParsedArg = DEFAULT_ARG;
            if (i < separatedArgStrings.length) {
                newParsedArg = Integer.parseInt(separatedArgStrings[i]);
            } else {
                System.err.println("Not enough arguments, arguments expected: " + expectedArgs);
            }
            parsedArgs[i] = newParsedArg;
        }
        return parsedArgs;
    }

    /**
     * Command interface
     */
    private interface Command {
        boolean execute(Path p, String args);
    }



    /*************************************
                Commands Start
     *************************************/

    public final static Command Collect = (p, args) -> {
        p.collectNode();
        return true;
    };

    public final static Command GotoStartingNode = (p, args) -> {
        p.goToStartingNode();
        return true;
    };

    public final static Command GoForward = (p, args) -> {
        int expectedArgs = 2;
        int[] parsedArgs = parseArgs(args, expectedArgs);

        int minSteps = parsedArgs[0];
        int maxSteps = parsedArgs[1];

        int stepsToTake = p.gen.ranRange(minSteps, maxSteps);
        int stepsTaken = 0;

        while (stepsTaken < stepsToTake) {
            if (p.attemptGoForward()) {
                stepsTaken++;
            }
        }

        return true;
    };

    public final static Command GoBackward = (p, args) -> {
        int expectedArgs = 2;
        int[] parsedArgs = parseArgs(args, expectedArgs);

        int minSteps = parsedArgs[0];
        int maxSteps = parsedArgs[1];

        int stepsToTake = p.gen.ranRange(minSteps, maxSteps);
        int stepsTaken = 0;

        while (stepsTaken < stepsToTake) {
            if (p.attemptGoBackward()) {
                stepsTaken++;
            }
        }

        return true;
    };

    public final static Command GoSide = (p, args) -> {
        int expectedArgs = 2;
        int[] parsedArgs = parseArgs(args, expectedArgs);

        int minSteps = parsedArgs[0];
        int maxSteps = parsedArgs[1];

        int stepsToTake = p.gen.ranRange(minSteps, maxSteps);
        int stepsTaken = 0;

        while (stepsTaken < stepsToTake) {
            if (p.attemptGoSide()) {
                stepsTaken++;
            }
        }

        return true;
    };


    /*************************************
     Commands End
     *************************************/


    /**
     * Path Style Misc
     */


    ArrayList<Command> commands;
    ArrayList<String> argStrings;
    int pointer;
    Path parent;

    public PathStyle() {
        commands = new ArrayList<>();
        argStrings = new ArrayList<>();
        pointer = 0;
    }

    public PathStyle addCommand(Command command, String argsString) {
        commands.add(command);
        argStrings.add(argsString);
        return this;
    }
    public PathStyle addCommand(Command command) {
        commands.add(command);
        argStrings.add("");
        return this;
    }

    public void setParentPath(Path parent) {
        this.parent = parent;
    }

    public void apply() {
        int size = Math.min(commands.size(), argStrings.size());
        if (size <= 0) return;

        Command nextCommand = commands.get(pointer);
        String nextArgsString = argStrings.get(pointer);

        nextCommand.execute(parent, nextArgsString);

        // Advance the pointer
        pointer++; pointer %= size;
    }

}
