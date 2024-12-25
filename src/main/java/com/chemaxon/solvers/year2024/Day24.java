package com.chemaxon.solvers.year2024;

import com.chemaxon.Solver;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.jsoup.internal.StringUtil;

import java.util.*;

public class Day24 extends Solver {
    enum Operation {
        AND, OR, XOR;
    }

    record Gate(String left, String right, String target, Operation op) {
        static Gate of(String s1, String s2, String target, Operation op) {
            return s1.compareTo(s2) < 0 ? new Gate(s1, s2, target, op) : new Gate(s2, s1, target, op);
        }

        boolean isSimilar(String s1, String s2, String target, Operation op) {
            return (s1 == null || s1.equals(left) || s1.equals(right))
                    && (s2 == null || s2.equals(left) || s2.equals(right))
                    && (target == null || target.equals(this.target))
                    && (op == null || op == this.op);
        }
    }

    Map<String, Boolean> wires = new HashMap<>();
    List<Gate> gates = new ArrayList<>();


    BidiMap<String, String> aliases = new DualHashBidiMap<>();
    BidiMap<String, String> confirmed = new DualHashBidiMap<>();
    BidiMap<String, String> suspicious = new DualHashBidiMap<>();


    public Day24(List<String> input) {
        super(input);
        var gates = false;
        for (String s : input) {
            if (s.trim().isEmpty()) {
                gates = true;
                continue;
            }

            if (!gates) {
                var dat = s.split(":");
                wires.put(dat[0], Integer.parseInt(dat[1].trim()) == 1);
            } else {
                var dat = s.split(" ");
                var w1 = dat[0];
                var w2 = dat[2];
                var op = dat[1].equals("AND") ? Operation.AND : dat[1].equals("OR") ? Operation.OR : Operation.XOR;
                var target = dat[4];
                this.gates.add(Gate.of(w1, w2, target, op));
                wires.putIfAbsent(w1, null);
                wires.putIfAbsent(w2, null);
                wires.putIfAbsent(target, null);
            }
        }

        LOGGER.info("{} gates {} wires", this.gates.size(), wires.size());
        LOGGER.info("z values: {}", values("z"));

    }

    long values(String startsWith) {
        List<String> zs = new ArrayList<>(wires.keySet().stream().filter(s -> s.startsWith(startsWith)).toList());
        var ready = zs.stream().allMatch(z -> wires.get(z) != null);
        if (!ready) {
            return -1;
        }
        zs.sort(Comparator.comparing(s -> Integer.parseInt(s.replace("z", ""))));
        zs = zs.reversed();
        var sb = new StringBuilder();
        zs.forEach(z -> sb.append(wires.get(z) ? 1 : 0));
        return Long.parseLong(sb.toString(), 2);
    }

    boolean isStatusOk() {
        long x = values("x");
        long y = values("y");
        long z = values("z");

        return x > -1 && y > -1 && z > -1 && x + y == z;
    }

    @Override
    public String solvePart1() {
        long z = -1;
        int opIdx = 0;
        while ((z = values("z")) == -1) {
            simulate(opIdx);
            opIdx = (opIdx + 1) % gates.size();
        }
        return String.valueOf(z);
    }

    private void simulate(int opIdx) {
        var gate = gates.get(opIdx);
        var res = getOpResult(gate);
        wires.put(gate.target, res);
    }

    private Boolean getOpResult(Gate gate) {
        if (wires.get(gate.left) == null || wires.get(gate.right) == null) {
            return null;
        }

        return switch (gate.op) {
            case AND -> wires.get(gate.left) && wires.get(gate.right);
            case OR -> wires.get(gate.left) || wires.get(gate.right);
            case XOR -> wires.get(gate.left) ^ wires.get(gate.right);
        };
    }

    Gate find(String s1, String s2, String target, Operation op) {
        return this.gates.stream().filter(g -> g.isSimilar(s1, s2, target, op)).findFirst().orElse(null);
    }


    @Override
    public String solvePart2() {

        //THIS IS A TERRIBLE JUGGLING WHERE I FIND THE SWAPPED NODES ONE BY ONE.
        //THE RESULT IS AT THE END.


        var xor00 = find("x00", "y00", null, Operation.XOR);
//        aliases.put(and00.target, "and00"); //this is actually z00
        var and00 = find("x00", "y00", null, Operation.AND);
//        aliases.put(and00.target, "and00"); //sorry, cannot put this in aliases, it is needed for m00
        var m00 = and00;
        aliases.put(m00.target, "m00");

        for (int i = 1; i < 45; i++) {
            System.out.println(format(i));
            var xori = find("x" + format(i), "y" + format(i), null, Operation.XOR);
            aliases.put(xori.target, "xor" + format(i));
            var andi = find("x" + format(i), "y" + format(i), null, Operation.AND);
            aliases.put(andi.target, "and" + format(i));

            var mi_1 = aliases.getKey("m" + format(i - 1));
            var xori_target = xori.target;


            var zi = find(xori_target,
                    mi_1,
                    "z" + format(i),
                    Operation.XOR);

            var pi = find(xori_target, mi_1, null, Operation.AND);
            if (pi == null) {
                System.out.println("pi is null");
                System.out.println("i suspect mi-1");
                System.out.println("check zi:");
                System.out.println(zi);
                System.out.println("alternative for zi");
                var instead = find(null, null, "z"+format(i), Operation.XOR);
                System.out.println(instead);
                System.out.println(aliases.get(instead.left));
                System.out.println(aliases.get(instead.right));
//                System.out.println("so "+instead.left + "should be mi-1");
                System.out.println("wait, and30 and xor30 are maybe swapped???");
                System.out.println("swap: " +aliases.getKey("and30") + " " +aliases.getKey("xor30") );
                break;
            }
            var mi = find(andi.target, pi.target, null, Operation.OR);

            if (zi == null) {
                System.out.println("zi is null");
                System.out.println("found instead:");
                var instead = find(xori_target, mi_1, null, Operation.XOR);
                System.out.println(instead);
                System.out.println("swap: "+ instead.target + " z" + format(i));
                break;
            }



            if (mi == null) {
                System.out.println("mi is null");
                break;
            }


//            System.out.println("xori " + xori);
//            System.out.println("andi " + andi);
//            System.out.println("mi-1 " + mi_1);
//            System.out.println("zi " + zi);
//            System.out.println("pi " + pi);
//            System.out.println("mi " + mi);

//            System.out.println();
        }

        String[] res = {"nnf",
                "z09",
                "nhs",
                "z20",
                "kqh",
                "ddn",
                "wrc",
                "z34"};
        Arrays.sort(res);

        return StringUtil.join(res, ",");
    }

    String format(int i) {
        return String.format("%02d", i);
    }
}
