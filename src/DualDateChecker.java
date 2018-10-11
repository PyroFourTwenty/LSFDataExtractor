class DateChecker {

    public  String DateChecker (int a, int b){
        if(a<b)
            return "a ist kleiner als b";
        else if (b<a){
            return "b ist kleiner als a";
        }
        else if (b==a)
            return "a und b sind gleich";
        return"Hmm tja iwas funzt nicht";
    }

}
