package lab2compiladores;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;

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

    private HashMap<Character, N> followingSetsDependencies;
    
    private HashMap<Character, Integer> tMTableIndexes;
    private HashMap<Character, Integer> nMTableIndexes;
    private String[][] MTable;

    public ASD(String fileUrl) throws IOException {
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
        
        generateMTable();
    }

    public char getS() {
        return S;
    }

    public ArrayList<Character> getNI() {
        return NI;
    }

    public ArrayList<Character> getT() {
        return T;
    }

    public HashMap<Character, ArrayList<String>> getPI() {
        return PI;
    }

    public ArrayList<Character> getNF() {
        return NF;
    }

    public HashMap<Character, ArrayList<String>> getPF() {
        return PF;
    }
    
    

    public HashMap<Character, HashSet<Character>> getFirstSets() {
        return firstSets;
    }

    public HashMap<Character, HashSet<Character>> getFollowingSets() {
        return followingSets;
    }

    private void compileGrammar(String fileUrl) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileUrl)));

        while (br.ready()) {
            String[] v = br.readLine().split("->");

            if (!NI.contains(v[0].charAt(0))) {
                NI.add(v[0].charAt(0));
            }
            if (!NF.contains(v[0].charAt(0))) {
                NF.add(v[0].charAt(0));
            }

            PI.putIfAbsent(v[0].charAt(0), new ArrayList<>());
            PI.get(v[0].charAt(0)).add(v[1]);
            PF.putIfAbsent(v[0].charAt(0), new ArrayList<>());
            PF.get(v[0].charAt(0)).add(v[1]);
            for (int i = 0; i < v[1].length(); i++) {
                if ('A' > v[1].charAt(i) || 'Z' < v[1].charAt(i)) {
                    T.add(v[1].charAt(i));
                }
            }
        }

        this.S = NI.get(0);
    }

    private void removeRecursivity() {

        HashMap<Character, Character> newNs = new HashMap<>();
        ArrayList<Character> auxN = new ArrayList<>(NF);
        ArrayList<Character> usedNs = new ArrayList<>(NF);
        HashMap<Character, ArrayList<String>> auxP = PF;

        NF = new ArrayList<>();
        PF = new HashMap<>();

        auxN.forEach((c) -> {
            if (auxP.get(c).stream().filter((s) -> (c == s.charAt(0))).count() > 0) {
                newNs.put(c, newNoTerminal(usedNs));
                NF.add(c);
                NF.add(newNs.get(c));
                PF.putIfAbsent(c, new ArrayList<>());
                PF.putIfAbsent(newNs.get(c), new ArrayList<>());

                auxP.get(c).forEach((s) -> {
                    if (c == s.charAt(0)) {
                        PF.get(newNs.get(c)).add(s.substring(1) + String.valueOf(newNs.get(c)));
                    } else {
                        PF.get(c).add(s + String.valueOf(newNs.get(c)));
                    }
                });
                PF.get(newNs.get(c)).add("&");
            } else {
                NF.add(c);
                PF.putIfAbsent(c, new ArrayList<>());
                auxP.get(c).forEach(s -> {
                    PF.get(c).add(s);
                });
            }
        });
    }

    private void factoring() {
        boolean keep = true;

        while (keep) {
            keep = false;

            HashMap<Character, Character> newNs = new HashMap<>();
            ArrayList<Character> auxN = new ArrayList<>(NF);
            ArrayList<Character> usedNs = new ArrayList<>(NF);
            HashMap<Character, ArrayList<String>> auxP = PF;

            NF = new ArrayList<>();
            PF = new HashMap<>();

            for (Character c : auxN) {
                auxP.get(c).sort(null);
                int[] lcp = new int[auxP.get(c).size()];
                String prev = auxP.get(c).get(0);
                int maxLcp = 0, pos = -1;
                for (int i = 1; i < auxP.get(c).size(); i++) {
                    int min = Math.min(prev.length(), auxP.get(c).get(i).length());
                    for (int j = 0; j < min; j++) {
                        if (prev.charAt(j) == auxP.get(c).get(i).charAt(j)) {
                            lcp[i]++;
                        } else {
                            break;
                        }
                    }
                    if (lcp[i] >= maxLcp) {
                        maxLcp = lcp[i];
                        pos = i;
                    }
                    prev = auxP.get(c).get(i);
                }

                String lcpCad = null;
                NF.add(c);
                PF.put(c, new ArrayList<>());
                if (maxLcp > 0) {
                    lcpCad = auxP.get(c).get(pos).substring(0, maxLcp);
                    keep = true;
                    newNs.put(c, newNoTerminal(usedNs));
                    NF.add(newNs.get(c));
                    PF.put(newNs.get(c), new ArrayList<>());

                    PF.get(c).add(lcpCad + String.valueOf(newNs.get(c)));
                }
                for (String s : auxP.get(c)) {
                    if (maxLcp > 0 && s.length() >= maxLcp && s.substring(0, maxLcp).equals(lcpCad)) {
                        PF.get(newNs.get(c)).add(s.substring(maxLcp).equals("") ? String.valueOf(VOID_CHAR) : s.substring(maxLcp));
                    } else {
                        PF.get(c).add(s);
                    }
                }
            }
        }
    }

    private void firstSets() {
        NF.forEach((c) -> {
            if (!firstSets.containsKey(c)) {
                firstSets.put(c, firstSets(c));
            }
        });
    }

    private HashSet<Character> firstSets(Character c) {
        HashSet<Character> p = new HashSet<>();
        if ('A' > c || c > 'Z') {
            p.add(c);
            return p;
        }
        if (firstSets.containsKey(c)) {
            return firstSets.get(c);
        }

        PF.get(c).forEach((s) -> {
            HashSet<Character> aux;
            boolean sw = !p.contains(VOID_CHAR);
            int index = 0;
            do {
                aux = firstSets(s.charAt(index));
                p.addAll(aux);
                index++;
            } while (index < s.length() && aux.contains(VOID_CHAR));
            if (!aux.contains(VOID_CHAR) && sw) {
                p.remove(VOID_CHAR);
            }
        });
        return p;
    }

    public void followingSets() {
        this.followingSetsDependencies = new HashMap<>();

        NF.forEach((c) -> {
            followingSets.put(c, new HashSet<>());
        });
        followingSets.get(S).add('$');

        HashSet<Character> visited = new HashSet<>();
        NF.forEach(c -> {
            if (!visited.contains(c)) {
                followingSets(c, visited);
                visited.add(c);
            }
        });

        LinkedList<Character> stack = new LinkedList<>();
        for (Character c : followingSetsDependencies.keySet()) {
            if (followingSetsDependencies.get(c).incidents == 0) {
                stack.push(c);
            }
        }

        while (!stack.isEmpty()) {
            Character current = stack.pop();
            for (Character c : followingSetsDependencies.get(current).getNext()) {
                followingSetsDependencies.get(c).addOneToVisits();
                followingSets.get(c).addAll(followingSets.get(current));
                if (followingSetsDependencies.get(c).visits == followingSetsDependencies.get(c).incidents) {
                    stack.push(c);
                }
            }
        }

    }

    private void followingSets(Character c, HashSet<Character> visited) {
        for (String s : PF.get(c)) {
            for (int i = 0; i < s.length(); i++) {
                if ('A' <= s.charAt(i) && s.charAt(i) <= 'Z') {
                    int index = i + 1;
                    boolean sw = true;
                    while (index < s.length() && sw) {
                        HashSet<Character> aux = new HashSet<>(firstSets(s.charAt(index)));
                        sw = aux.remove(VOID_CHAR);
                        followingSets.get(s.charAt(i)).addAll(aux);
                        index++;
                    }
                    if (sw && c != s.charAt(i)) {
                        followingSetsDependencies.putIfAbsent(c, new N());
                        followingSetsDependencies.putIfAbsent(s.charAt(i), new N());

                        if (!followingSetsDependencies.get(c).getNext().contains(s.charAt(i))) {
                            followingSetsDependencies.get(c).getNext().add(s.charAt(i));
                            followingSetsDependencies.get(s.charAt(i)).addOneToIncidents();
                        }
                    }
                }
            }
        }
    }

    private Character newNoTerminal(ArrayList<Character> nt) {
        for (char c = 'A'; c <= 'Z'; c++) {
            if (!nt.contains(c)) {
                nt.add(c);
                return c;
            }
        }
        return null;
    }
    
    private void generateMTable(){
        tMTableIndexes = new HashMap<>();
        nMTableIndexes = new HashMap<>();
        MTable = new String[NF.size()][T.size()+1];
        
        int i = 0;
        for(Character c : T){
            tMTableIndexes.put(c, i);
            i++;
        }
        tMTableIndexes.put('$', i);
        
        i=0;
        for(Character c : NF){
            nMTableIndexes.put(c, i);
            for(String s : PF.get(c)){
                String value = c+" -> "+s;
                
                HashSet<Character> aux = new HashSet<>();
                int index = 0;
                do {
                    aux.remove(VOID_CHAR);
                    aux.addAll(firstSets(s.charAt(index)));
                    index++;
                } while (index < s.length() && aux.contains(VOID_CHAR));
                if (aux.contains(VOID_CHAR)) {
                    aux.remove(VOID_CHAR);
                    for(Character a : followingSets.get(c)){
                        MTable[nMTableIndexes.get(c)][tMTableIndexes.get(a)] = (MTable[nMTableIndexes.get(c)][tMTableIndexes.get(a)]==null?"":MTable[nMTableIndexes.get(c)][tMTableIndexes.get(a)])+value;
                    }
                }
                for(Character t : aux){
                    MTable[nMTableIndexes.get(c)][tMTableIndexes.get(t)] = (MTable[nMTableIndexes.get(c)][tMTableIndexes.get(t)]==null?"":MTable[nMTableIndexes.get(c)][tMTableIndexes.get(t)])+value;
                }
                
            }
            i++;
        }
        
        for (int k = 0; k < MTable.length; k++) {
            System.out.println(Arrays.toString(MTable[k]));
        }
        ;
    }
    
    public void showMTable(DefaultTableModel model){
        String[] id = new String[T.size()+2];
        String[][] data = new String[NF.size()][T.size()+2];
        
        id[0] = "N\\T";
        int i=1, j=0;
        for(Character t : T){
            id[i] = String.valueOf(t);
            i++;
        }
        id[i] = "$";
        
        for(i = 0; i<NF.size(); i++){
            data[i][0] = String.valueOf(NF.get(i));
            for(j = 0; j<T.size()+1; j++){
                data[i][j+1] = MTable[i][j];
            }
        }
        
        model.setDataVector(data, id);
    }

    public void showFirstOrFollowing(JLabel label, HashMap<Character, HashSet<Character>> fof, String arg) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        NF.forEach((key) -> {
            sb.append(arg).append("(").append(key).append(") = ").append(fof.get(key).toString()).append("<br>");
        });
        sb.append("</html>");
        label.setText(sb.toString());
    }

    public void showNewGrammar(JLabel label) {
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

    private static class N {

        private int incidents;
        private int visits;
        private HashSet<Character> next;

        public N() {
            this.next = new HashSet<>();
            this.incidents = 0;
            this.visits = 0;
        }

        public HashSet<Character> getNext() {
            return next;
        }

        public int getIncidents() {
            return this.incidents;
        }

        public void setIncidents(int incidents) {
            this.incidents = incidents;
        }

        public int getVisits() {
            return this.visits;
        }

        public void setVisits(int visits) {
            this.visits = visits;
        }

        public void addOneToVisits() {
            this.visits++;
        }

        public void addOneToIncidents() {
            this.incidents++;
        }

        public void addNextN(Character c) {
            next.add(c);
        }

        @Override
        public String toString() {
            return this.next.toString();
        }
    }

}
