package in.aesh.align;

class Word {

    final int turnIndex;
    final String text;
    final String token;
    
    Word(int turnIndex, String text, String token) {
        this.turnIndex = turnIndex;
        this.text = text;
        this.token = token;
    }
    
    @Override
    public String toString() {
        return turnIndex + "|" + text + "|" + token;
    }
}
