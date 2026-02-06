/**
 * @author Rodrigo Afonso Cardoso
 * @notes:
 * The AI assistant Google Gemini was used for debugging purposes in the checkPosStatus, findEmptySlot and updateOpponentSpeed methods.
 */

import java.util.Scanner;

/**
 * Constants
 */
final char START = 'S';
final char BOOST = '+';
final char DRAG = '-';
final char OIL = '!';
final String ACCEL_CMD = "accel";
final String SHOW_CMD = "show";
final String STATUS_CMD = "status";
final String QUIT_CMD = "quit";
final char[] PLAYERS = {'P', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i'};
final String MSG_RACE_ENDED = "Race ended: %c won the race!\n";
final String MSG_WIN = "Player %c won the race!\n";
final String MSG_POSITION = "Player %c: cell %d, laps %d!\n";
final String MSG_PLAYER_NOT_FOUND = "Player %c does not exist!\n";
final String MSG_NOT_OVER = "The race is not over yet!";
final String MSG_COMMAND_NOT_FOUND = "Invalid command";

/**
 * Global Variables
 */
char[] trackArray;
char[] playersInTrack;
int[] positions;
int[] speed;
int[] laps;
char winner;
int win;
int yellowFlag;
int quitVal;


/**
 * Initializes the global Variables
 * @param track: track layout.
 * @param numOpponents; number of AI opponents.
 */
void initState(String track, int numOpponents){
    trackArray = new char[track.length()];
    playersInTrack = new char[track.length()];
    positions = new int[numOpponents + 1];
    laps = new int[numOpponents + 1];
    speed = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    winner = '0';
    win = 0;
    yellowFlag = 0;
    quitVal = 0;
}

/**
 * Fills the track array with the track from the input string
 * @param track: track layout.
 */
void writeTrack(String track){
    for (int i = 0; i < trackArray.length; i++)
        trackArray[i] = track.charAt(i);
}

/**
 * Finds where the pole position is (position right before the starting position)
 * @param track: track layout.
 * @return the number of the cell where the pole position is.
 */
int findPolePos(String track){
    int polePos = -2;
    int i = 0;
    do {
        if ((track.charAt(i)) == START)
            polePos = i - 1 + trackArray.length;
        i++;
    } while (polePos == -2);
    return (polePos);
}

/**
 * Sets the players initial positions
 * @param pole: cell where the pole position is.
 * @param numOpponents: number of AI opponents.
 */
void setGrid(int pole, int numOpponents){
    for (int i = 0; i <= numOpponents; i++)
        positions[i] = (pole - i);
}

/**
 * Determines the artificial intelligence acceleration choice
 * @param maxSpeed: Maximum speed allowed.
 * @param i: Player index.
 * @return the value of the acceleration chosen by the artificial intelligence.
 */
int artificialIn(int maxSpeed, int i){
    int valAccel = 0;
    int n = 1;
    do{
        if ((trackArray[(positions[i] + n)%trackArray.length] == BOOST) && speed[i] < maxSpeed)
            valAccel = 1;
        n++;
    } while ((n <= 3) && (valAccel != 1));
    n = 1;
    if (valAccel == 0)
        do {
            if ((trackArray[(positions[i] + n)%trackArray.length] == OIL) && speed[i] > 0)
                valAccel = -1;
            n++;
        } while ((n <= 3) && (valAccel != -1));
    return valAccel;

}

/**
 * Applies the artificial intelligence acceleration choice and updates the opponents speed
 * @param maxSpeed: Maximum speed allowed.
 * @param numOpponents: Number of AI opponents.
 */
void updateOpponentSpeed(int maxSpeed, int numOpponents){
    for (int i = 1; i <= numOpponents; i++){
        int val = artificialIn(maxSpeed, i);
        speed[i] += val;
        oppControlSpeed(i, maxSpeed);
    }
}

/**
 * Applies the acceleration player P chose and makes sure his speed does not go out of the limits (0 to maxSpeed)
 * @param maxSpeed: maximum speed allowed.
 * @param val: acceleration value chosen by the user (-1, 0 or 1).
 */
void pControlSpeed(int maxSpeed, int val){
    if (speed[0] + val < 0)
        speed[0] = 0;
    else if (speed[0] + val > maxSpeed)
        speed[0] = maxSpeed;
    else
        speed[0] += val;
}

/**
 * Makes sure the opponents speed do not go out of the limits (0 to maxSpeed)
 * @param i: player index.
 * @param maxSpeed: maximum speed allowed.
 */
void oppControlSpeed(int i, int maxSpeed){
    if (speed[i] > maxSpeed)
        speed[i] = maxSpeed;
    else if (speed[i] < 0)
        speed[i] = 0;
}

/**
 * Checks if there is a winner yet (if any player has completed the number of laps needed)
 * @param i: player index.
 * @param raceLaps: number of laps needed to complete the race.
 */
void checkWinner(int i, int raceLaps){
    if (laps[i] >= raceLaps) {
        win++;
        winner = PLAYERS[i];
    }
}

/**
 * Updates the players laps count based on their absolute position
 * @param i: player index.
 * @param raceLaps: number of laps needed to complete the race.
 * @param pole: cell where the pole position is.
 */
void checkLapsComplete(int i, int raceLaps, int pole){
    laps[i] = ((positions[i] - (pole + 1))/trackArray.length);
    checkWinner(i, raceLaps);
}

/**
 * Applies the special effect if a player landed on a "BOOST", "DRAG" or "OIL" cell
 * @param i: player index.
 */
void checkNewPositions(int i){
    if (trackArray[(positions[i] % trackArray.length)] == BOOST) {
        speed[i] += 1;
    }
    else if (trackArray[(positions[i] % trackArray.length)] == DRAG) {
        speed[i] -= 1;
    }
    else if (trackArray[(positions[i] % trackArray.length)] == OIL)
        speed[i] = 0;
}

/**
 * Checks if a cell is already occupied by another player
 * @param slot: initial cell of destination.
 * @param numOpponents: number of AI opponents.
 * @param playerIndex: index of respective player on the arrays
 * @return value 1 if slot is occupied by another player and value 0 otherwise
 */
int checkPosStatus(int slot, int numOpponents, int playerIndex) {
    int val = 0;
    int i = 0;
    do {
        if (i != playerIndex && positions[i] % trackArray.length == slot % trackArray.length)
            val = 1;
        i++;
    } while ((i <= numOpponents) && (val != 1));
    return val;
}

/**
 * Finds the players round final positions, solving collisions
 * @param i: player index.
 * @param slot: initial cell of destination.
 * @param numOpponents: number of AI opponents.
 * @param move: number of cells the player was supposed to move (if there were no other players).
 */
void findEmptySlot(int i, int slot, int numOpponents, int move) {
    int foundPos = 0;
    int n = 1;
    if (checkPosStatus(slot, numOpponents, i) == 0) {
        positions[i] += move;
    }
    else {
        yellowFlag = 1;
        while (foundPos == 0 && n <= move ) {
            if (checkPosStatus(slot - n, numOpponents, i) == 0) {
                positions[i] += move - n;
                foundPos++;
            }
            n++;
        }
    }
}

/**
 * // Checks if the yellow flag is active in the current round
 * @return false if yellow flag is active and true otherwise.
 */
boolean checkYellowFlag(){
    return yellowFlag == 0;
}

/**
 * Moves the players based on their speed, taking in mind if the yellow flag is active or not
 * @param i: player index.
 * @param numOpponents: number of AI opponents
 */
void movePlayers(int i, int numOpponents){
    if (checkYellowFlag()) {
        findEmptySlot(i, (positions[i] + speed[i]), numOpponents, speed[i]);
    }
    else {
        if (speed[i] > 0) {
            findEmptySlot(i, positions[i] + 1, numOpponents, 1 );
        }
    }
}

/**
 * // Creates a copy of the track without any players on it
 */
void cloneTrack(){
    for (int i = 0; i < trackArray.length; i++)
        playersInTrack[i] = trackArray[i];
}

/**
 * Matches the players character ID with their array index
 * @param playerID: players character identifier (P, a, b, c, etc.).
 * @param numOpponents: number of AI opponents.
 * @return the value of the array index of the respective player ID.
 */
int processPlayerID(char playerID, int numOpponents){
    int val = -1;
    for (int i = 0; i <= numOpponents; i++)
        if (playerID == (PLAYERS[i]))
            val = i;
    return val;
}

/**
 * Processes all the players movements in a single round and state changes
 * @param numOpponents: number of AI opponents.
 * @param raceLaps: laps needed to complete the race.
 * @param pole: cell where the pole position is.
 * @param maxSpeed: maximum speed allowed.
 */
void processRound(int numOpponents, int raceLaps, int pole, int maxSpeed){
    yellowFlag = 0;
    int i;
    for (i = 0; i <= numOpponents; i++){
        if (win == 0){
            movePlayers(i, numOpponents);
            checkNewPositions(i);
            oppControlSpeed(i, maxSpeed);
            checkLapsComplete(i, raceLaps, pole);
        }
    }
}

/**
 * // Processes the entire race logic, from the race setup to the commands loop
 * @param in: scanner.
 * @param track: track layout.
 * @param numOpponents: number of AI opponents.
 * @param maxSpeed: maximum speed allowed.
 * @param raceLaps: laps needed to finish the race.
 */
void processRace(Scanner in, String track, int numOpponents, int maxSpeed, int raceLaps){
    int pole = (findPolePos(track));
    setGrid(pole, numOpponents);
    processCommand(in, numOpponents, maxSpeed, raceLaps, pole);
}

/**
 * Returns an integer from the Scanner and changes its line
 * @param in: scanner.
 * @return an integer from the scanner
 */
int readIntLn(Scanner in){
    int val = in.nextInt();
    in.nextLine();
    return val;
}

/**
 * Used if the accel command is selected: reads the accel input, processes the round and prints its results
 * @param in: scanner.
 * @param maxSpeed: maximum speed allowed.
 * @param numOpponents: number of AI opponents.
 * @param raceLaps: number of laps needed to finish the race.
 * @param pole: cell where the pole position is.
 */
void accel(Scanner in, int maxSpeed, int numOpponents, int raceLaps, int pole){
    int val = in.nextInt();
    if (win != 0) {
        System.out.printf(MSG_RACE_ENDED, winner);
    }
    else {
        pControlSpeed(maxSpeed, val);
        updateOpponentSpeed(maxSpeed, numOpponents);
        processRound(numOpponents, raceLaps, pole, maxSpeed);
        if (win != 0) {
            System.out.printf(MSG_WIN, winner);
        } else {
            System.out.printf(MSG_POSITION, 'P', positions[0] % trackArray.length, laps[0]);
        }
    }
}

/**
 * Prints the current race state (if it has ended yet or not), used if the show command is selected
 */
void outputRaceStatus(){
    if (win == 0)
        System.out.print(" (ongoing)\n");
    else
        System.out.print(" (ended)\n");
}

/**
 * Prints the track and the players on their respective positions, used if the show command is selected
 */
void outputTrack(){
    for (int i = 0; i < trackArray.length; i++)
        System.out.print(playersInTrack[i]);
}

/**
 * Used if the show command is selected: prints the track and race state
 * @param numOpponents: number of AI opponents.
 */
void show(int numOpponents){
    cloneTrack();
    for (int i = 0; i <= numOpponents; i++)
        playersInTrack[(positions[i]%trackArray.length)] = PLAYERS[i];
    outputTrack();
    outputRaceStatus();
}

/**
 * Used if the status command is selected: prints the position and current lap of a specific player (chosen by the user)
 * @param in: scanner.
 * @param numOpponents: number of AI opponents.
 */
void status(Scanner in, int numOpponents){
    char playerID = in.next().charAt(0);
    int pNum = processPlayerID(playerID, numOpponents);
    if (playerID == winner)
        System.out.printf(MSG_RACE_ENDED, playerID);
    else if (processPlayerID(playerID, numOpponents) == -1)
        System.out.printf(MSG_PLAYER_NOT_FOUND, playerID);
    else
        System.out.printf(MSG_POSITION, playerID, positions[pNum]%trackArray.length, laps[pNum]);
}

/**
 * Used if the quit command is selected: it finishes the program and prints the final race state
 */
void quit(){
    quitVal++;
    if (win == 0)
        System.out.println(MSG_NOT_OVER);
    else {
        System.out.printf(MSG_RACE_ENDED, winner);
    }
}

/**
 * Reads the next command selected by the user
 * @param inp: scanner.
 * @param maxSpeed: maximum speed allowed.
 * @param numOpponents: number of AI opponents.
 * @param raceLaps: number of laps needed to finish the race.
 * @param pole: cell where the pole position is.
 */
void readCommand(Scanner inp, int maxSpeed, int numOpponents, int raceLaps, int pole){
    switch (inp.next()){
        case (ACCEL_CMD):
            accel(inp, maxSpeed, numOpponents, raceLaps, pole);
            break;
        case (SHOW_CMD):
            show(numOpponents);
            break;
        case (STATUS_CMD):
            status(inp, numOpponents);
            break;
        case (QUIT_CMD):
            quit();
            break;
        default:
            System.out.println(MSG_COMMAND_NOT_FOUND);
    }
    inp.nextLine();
}

/**
 * Reads commands until the user selects the quit command (as it finishes the program)
 * @param in: scanner.
 * @param numOpponents: number of AI opponents.
 * @param maxSpeed: maximum speed allowed.
 * @param raceLaps: number of laps needed to finish the race.
 * @param pole: cell where the pole position is.
 */
void processCommand(Scanner in, int numOpponents, int maxSpeed, int raceLaps, int pole){
    do {
        readCommand(in, maxSpeed, numOpponents, raceLaps, pole);
    } while (quitVal == 0);
}

void main(){
    Scanner sc = new Scanner(System.in);
    String track = sc.nextLine();
    int raceLaps = sc.nextInt();
    int maxSpeed = sc.nextInt();
    int numOpponents = readIntLn(sc);
    initState(track, numOpponents);
    writeTrack(track);
    processRace(sc, track, numOpponents, maxSpeed, raceLaps);
    sc.close();
}