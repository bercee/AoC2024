package com.chemaxon.solvers.year2024;

import com.chemaxon.Solver;
import org.jsoup.internal.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//SORRY NO REFACTOR TODAY EITHER. I AM IN A RUSH.

public class Day17 extends Solver {

    record Output(long a, long b) {
    }

    interface Instruction {
        void exec(int operand);
    }

    static class Sim {

        int A;
        int B;
        int C;

        List<Integer> input;
        int pointer;
        List<Integer> output = new ArrayList<>();


        //0
        Instruction adv = op -> A = A / (int) Math.pow(2, getComboOperand(op));
        //1
        Instruction bxl = op -> B = B ^ op;
        //2
        Instruction bst = op -> B = getComboOperand(op) % 8;
        //3
        Instruction jnz = op -> {
            if (A != 0) {
                pointer = op - 2;
            }
//            LOGGER.info("\t\t{}\t\t{}\t\t{}", A, B, C);
        };
        //4
        Instruction bxc = op -> B = B ^ C;
        //5
        Instruction out = op -> output.add(getComboOperand(op) % 8);
        //6
        Instruction bdv = op -> B = A / (int) Math.pow(2, getComboOperand(op));
        //7
        Instruction cdv = op -> C = A / (int) Math.pow(2, getComboOperand(op));


        Instruction[] instructions = new Instruction[]{
                adv, bxl, bst, jnz, bxc, out, bdv, cdv
        };

        public Sim(int a, int b, int c, List<Integer> input) {
            A = a;
            B = b;
            C = c;
            this.input = input;
            this.pointer = 0;

//            LOGGER.info("A {} B {} C {}", A, B, C);
//            LOGGER.info("input {}", this.input);
        }

        int getComboOperand(int o) {
            return switch (o) {
                case 0, 1, 2, 3 -> o;
                case 4 -> A;
                case 5 -> B;
                case 6 -> C;
                default -> throw new RuntimeException("Supposed to not happen");
            };
        }

        void execute() {
            instructions[input.get(pointer)].exec(input.get(pointer + 1));
            pointer += 2;
        }
    }


    Sim sim;

    int A;
    int B;
    int C;
    List<Integer> input;


    public Day17(List<String> input) {
        super(input);
        A = Integer.parseInt(input.get(0).split(":")[1].trim());
        B = Integer.parseInt(input.get(1).split(":")[1].trim());
        C = Integer.parseInt(input.get(2).split(":")[1].trim());

        this.input = Arrays.stream(input.get(4).split(": ")[1].split(",")).mapToInt(Integer::parseInt).boxed().collect(Collectors.toList());

    }


    @Override
    public String solvePart1() {
        sim = new Sim(A, B, C, this.input);
        while (sim.pointer >= 0 && sim.pointer < sim.input.size()) {
            sim.execute();
        }

        return sim.output.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    @Override
    public String solvePart2() {

//        2,4,1,1,7,5,4,4,1,4,0,3,5,5,3,0
//
//        2,4, //B = A%8
//        1,1, //B = B XOR 1
//        7,5, //C = A / 2^B
//        4,4, //B = B XOR C
//        1,4, //B = B XOR 4
//        0,3, //A = A/8
//        5,5, //prints B%8
//        3,0  //jumps to the start if A > 0


        var reverseList =  this.input.reversed();
//        var reverseList = Arrays.stream("7,4,2,0,5,0,5,3,7".split(",")).mapToLong(Long::parseLong).boxed().toList().reversed();
        List<List<Long>> possibleALists = new ArrayList<>();


        for (int i = 0; i < reverseList.size(); i++) {
            long targetOut = reverseList.get(i);
            ArrayList<Long> possibleAs = new ArrayList<>();
            List<Long> prevList = i == 0 ? List.of(0L) : possibleALists.get(i - 1);

            for (Long possibleA : prevList) {
                var min = possibleA * 8;
                var max = min + 8;
                for (long j = min; j < max; j++) {
                    var out = processA(j);
                    if (out.b % 8 == targetOut) {
                        possibleAs.add(j);
                    }
                }
            }
            System.out.println("step" + i);
            System.out.println(possibleAs);

            possibleALists.add(possibleAs);
        }

        System.out.println("looking for:");
        System.out.println(StringUtil.join(reverseList.reversed(), ","));
        long ret = 0L;
        StringBuilder sb = new StringBuilder();
        for (Long loring : possibleALists.getLast()) {
            long l = loring;
            System.out.println(l);
            Output out1;
            while ((out1 = processA(l)).a != 0) {
                l = out1.a;
                sb.append(out1.b %8).append(",");
            }
            sb.append(out1.b%8);
            System.out.println(sb.toString());
            if (sb.toString().equals(StringUtil.join(reverseList.reversed(), ","))) {
                ret = loring;
                break;
            }
        }


        return convert(ret);
    }





    Output processA(long A) {
        long a = A;
        long b;
        long c;

        b = a % 8;
        b = b ^ 1;
        c = a / (long) Math.pow(2, b);
        b = b ^ c;
        b = b ^ 4;
        a = a / 8;


        return new Output(a, b);
    }

}
