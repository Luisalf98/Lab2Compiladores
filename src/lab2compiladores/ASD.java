
package lab2compiladores;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.JLabel;

public class ASD {
    
    public static final char VOID_CHAR = '&';
    
    private char S;
    private final ArrayList<Character> NI;
    private final ArrayList<Character> T;
    private final HashMap<Character, ArrayList<String>> PI;
    
    private ArrayList<Character> NF;
    private HashMap<Character, ArrayList<String>> PF;
    
    private final HashMap<Character, HashSet<Character>> firstSets;
    private final HashMap<Character, HashSet<Character>> followingSets;
    
    public ASD(String fileUrl) throws IOException{
        this.NI = new ArrayList<>();
        this.T = new ArrayList<>();
        this.PI = new HashMap<>();
        
        this.NF = new ArrayList<>();
        this.PF = new HashMap<>();
        
        firstSets = new HashMap<>();
        followingSets = new HashMap<>();
        
        compileGrammar(fileUrl);
        
        removeRecursivity();
        factoring();    
        
        firstSets();
        followingSets();
    }

    public HashMap<Character, HashSet<Character>> getFirstSets() {
        return firstSets;
    }

    public HashMap<Character, HashSet<Character>> getFollowingSets() {
        return followingSets;
    }
    
    private void compileGrammar(String fileUrl) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileUrl)));
        
        while(br.ready()){
            String[] v = br.readLine().split("->");
            
            if(!NI.contains(v[0].charAt(0))) NI.add(v[0].charAt(0));
            if(!NF.contains(v[0].charAt(0))) NF.add(v[0].charAt(0));
            
            PI.putIfAbsent(v[0].charAt(0), new ArrayList<>());
            PI.get(v[0].charAt(0)).add(v[1]);
            PF.putIfAbsent(v[0].charAt(0), new ArrayList<>());
            PF.get(v[0].charAt(0)).add(v[1]);
            for (int i = 0; i < v[1].length(); i++) {
                if('A'>v[1].charAt(i) || 'Z'<v[1].charAt(i)){
                    T.add(v[1].charAt(i));
                }
            }
        }
        
        this.S = NI.get(0);
    }
    
    private void removeRecursivity(){
        
        HashMap<Character, Character> newNs = new HashMap<>();
        ArrayList<Character> auxN = new ArrayList<>(NF);
        ArrayList<Character> usedNs = new ArrayList<>(NF);
        HashMap<Character, ArrayList<String>> auxP = PF;
        
        NF = new ArrayList<>();
        PF = new HashMap<>();
        
        auxN.forEach((c) -> {
            if(auxP.get(c).stream().filter((s) -> (c == s.charAt(0))).count()>0){
                newNs.put(c, newNoTerminal(usedNs));
                NF.add(c);
                NF.add(newNs.get(c));
                PF.putIfAbsent(c, new ArrayList<>());
                PF.putIfAbsent(newNs.get(c), new ArrayList<>());
                
                auxP.get(c).forEach((s)->{
                    if(c == s.charAt(0)){
                        PF.get(newNs.get(c)).add(s.substring(1)+String.valueOf(newNs.get(c)));
                    }else{
                        PF.get(c).add(s+String.valueOf(newNs.get(c)));
                    }
                });
                PF.get(newNs.get(c)).add("&");
            }else{
                NF.add(c);
                PF.putIfAbsent(c, new ArrayList<>());
                auxP.get(c).forEach(s->{
                    PF.get(c).add(s);
                });
            }
        });
    }
    
    private void factoring(){
        boolean keep = true;
        
        while(keep){
            keep = false;
            
            HashMap<Character, Character> newNs = new HashMap<>();
            ArrayList<Character> auxN = new ArrayList<>(NF);
            ArrayList<Character> usedNs = new ArrayList<>(NF);
            HashMap<Character, ArrayList<String>> auxP = PF;

            NF = new ArrayList<>();
            PF = new HashMap<>();

            for(Character c : auxN){
                auxP.get(c).sort(null);
                int[] lcp = new int[auxP.get(c).size()];
                String prev = auxP.get(c).get(0);
                int maxLcp = 0, pos = -1;
                for (int i = 1; i < auxP.get(c).size(); i++) {
                    int min = Math.min(prev.length(), auxP.get(c).get(i).length());
                    for (int j = 0; j < min; j++) {
                        if(prev.charAt(j)==auxP.get(c).get(i).charAt(j)){
                            lcp[i]++;
                        }else{
                            break;
                        }
                    }
                    if(lcp[i]>=maxLcp){
                        maxLcp = lcp[i];
                        pos = i;
                    }
                    prev = auxP.get(c).get(i);
                }
                

                String lcpCad = null;
                NF.add(c);
                PF.put(c, new ArrayList<>());
                if(maxLcp>0){
                    lcpCad = auxP.get(c).get(pos).substring(0, maxLcp);
                    keep = true;
                    newNs.put(c, newNoTerminal(usedNs));
                    NF.add(newNs.get(c));
                    PF.put(newNs.get(c), new ArrayList<>());

                    PF.get(c).add(lcpCad+String.valueOf(newNs.get(c)));
                }
                for(String s : auxP.get(c)){
                    if(maxLcp>0 && s.length()>=maxLcp && s.substring(0, maxLcp).equals(lcpCad)){
                        PF.get(newNs.get(c)).add(s.substring(maxLcp).equals("")?String.valueOf(VOID_CHAR):s.substring(maxLcp));
                    }else{
                        PF.get(c).add(s);
                    }
                }
            }
        }
    }
    
    private void firstSets(){
        NF.forEach((c) -> {
            if(!firstSets.containsKey(c)){
                firstSets.put(c, firstSets(c));
            }
        });
    }
    
    private HashSet<Character> firstSets(Character c){
        HashSet<Character> p = new HashSet<>();
        if('A'>c || c>'Z'){
            p.add(c);
            return p;
        }
        if(firstSets.containsKey(c)){
            return firstSets.get(c);
        }
        
        PF.get(c).forEach((s) -> {
            HashSet<Character> aux;
            int index = 0;
            do{
                aux = firstSets(s.charAt(index));
                p.addAll(aux);
                index++;
            }while(index<s.length() && s.charAt(index-1)!=VOID_CHAR && aux.contains(VOID_CHAR));
        });
        return p;
    }
    
    public void followingSets(){
        NF.forEach((c) -> {
            followingSets.put(c, new HashSet<>());
        });
        followingSets.get(S).add('$');
        
        HashSet<Character> visited = new HashSet<>();
        NF.forEach(c->{
            if(!visited.contains(c)){
                followingSets(c, visited);
                visited.add(c);
            }
        });
                
    }
    
    private void followingSets(Character c, HashSet<Character> visited){
        for(String s : PF.get(c)){
            for(int i=0; i<s.length(); i++){
                if('A'<=s.charAt(i) && s.charAt(i)<='Z'){
                    int index = i+1;
                    boolean sw = true;
                    while(index<s.length() && sw){
                        HashSet<Character> aux = new HashSet<>(firstSets(s.charAt(index)));
                        sw = aux.remove(VOID_CHAR);
                        followingSets.get(s.charAt(i)).addAll(aux);
                        index++;
                    }
                }
            }
        }
    }
    
    private Character newNoTerminal(ArrayList<Character> nt){
        for(char c = 'A'; c<='Z'; c++){
            if(!nt.contains(c)){
                nt.add(c);
                return c;
            }
        }
        return null;
    }
    
    public void showFirstOrFollowing(JLabel label, HashMap<Character, HashSet<Character>> fof, String arg){
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        NF.forEach((key) -> {
            sb.append(arg).append("(").append(key).append(") = ").append(fof.get(key).toString()).append("<br>");
        });
        sb.append("</html>");
        label.setText(sb.toString());
    }
    
    public void showNewGrammar(JLabel label){
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        NF.forEach((key) -> {
            PF.get(key).forEach(s -> {
                sb.append(key).append(" &rarr; ").append(s).append("<br>");
            });
        });
        sb.append("<br><br>").append("S = ").append(S);
        sb.append("</html>");
        label.setText(sb.toString());
    }
    
}
