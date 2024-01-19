package lab1.sirichat.project1;

import java.util.Map;

public class SiriProtocol {

    private static final int IDLE = 0;
    private static final int SENTPROMPT = 1;
    private static final int ANOTHER = 2;
    private int state = IDLE;


    private final Map<String,String> questionAnswer = Map.of(
            "who created you?","The apple engineers!",
            "what does siri mean?","Beautiful woman who leads you to victory!",
            "are you a robot?","I could never be a robot :)");

    public String processInput(String theInput) {
        String theOutput = null;

        if (state == IDLE) {
            theOutput = "Siri here! Ask me anything!";
            state = SENTPROMPT;
        } else if (state == SENTPROMPT) {

            if (questionAnswer.containsKey(theInput.toLowerCase())) {
                theOutput = questionAnswer.get(theInput.toLowerCase())+" Would you like to ask another question? (y/n)";
                state = ANOTHER;
            } else {
                theOutput = "Please ask a correct question. I'm not that smart!";
            }

        }

        else if (state == ANOTHER) {
            if (theInput.equalsIgnoreCase("y")) {
                theOutput = "Ask another question: ";
                state = SENTPROMPT;
            } else {
                theOutput = "Bye.";
                state = IDLE;
            }
        }
        return theOutput;
    }
}
