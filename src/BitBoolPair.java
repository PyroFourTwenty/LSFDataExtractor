public class BitBoolPair{
    String bit;
    boolean isPossible = true;

    /**
     * By default, every combination is considered possible, when generated
     * @param bit
     */
    public BitBoolPair(String bit){
        this.bit = bit;
    }
    public boolean getPossible(){
        return this.isPossible;
    }
    public void setPossible (boolean b){
        this.isPossible = b;
    }
    public String toString(){
        return "Bits: "+this.bit+" --> isPossible: "+this.isPossible;
    }

}


