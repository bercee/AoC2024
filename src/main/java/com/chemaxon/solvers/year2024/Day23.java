package com.chemaxon.solvers.year2024;

import com.chemaxon.Solver;
import org.jgrapht.Graph;
import org.jgrapht.alg.clique.BronKerboschCliqueFinder;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jsoup.internal.StringUtil;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Day23 extends Solver {

    record OrderedSet(
            String s1,
            String s2,
            String s3) {

        static OrderedSet of(String s1, String s2, String s3) {
            var l = List.of(s1, s2, s3);
            return of(l);
        }

        static OrderedSet of(List<String> l) {
            var lo = new ArrayList<String>(l).stream().sorted().toList();
            return new OrderedSet(lo.get(0), lo.get(1), lo.get(2));
        }

        boolean anyMatch(Predicate<String> p) {
            return p.test(s1) || p.test(s2) || p.test(s3);
        }


    }

    Graph<String, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);

    public Day23(List<String> input) {
        super(input);

        for (String s : input) {
            var dat = s.split("-");
            Arrays.stream(dat).forEach(graph::addVertex);
            graph.addEdge(dat[0], dat[1]);
        }

        LOGGER.info("vertexes {} edges {}", graph.vertexSet().size(), graph.edgeSet().size());
    }

    Set<String> getNeighbors(String s) {
        return graph.edgesOf(s).stream().map(e -> getOtherVertex(e, s)).collect(Collectors.toSet());
    }

    String getOtherVertex(DefaultEdge edge, String vertex) {
        return graph.getEdgeTarget(edge).equals(vertex) ? graph.getEdgeSource(edge) : graph.getEdgeTarget(edge);
    }

    @Override
    public String solvePart1() {
        Set<OrderedSet> sets = new HashSet<>();
        for (String s : new HashSet<>(graph.vertexSet())) {
            var neighbors = getNeighbors(s);
            for (String neighbor : neighbors) {
                var secondNeighbors = getNeighbors(neighbor);
                var commonNeighbors = neighbors.stream().filter(secondNeighbors::contains).collect(Collectors.toSet());
                for (String commonNeighbor : commonNeighbors) {
                    sets.add(OrderedSet.of(s, neighbor, commonNeighbor));
                }
            }
        }

        return String.valueOf(sets.stream().filter(g -> g.anyMatch(t -> t.startsWith("t"))).count());
    }

    boolean areConnected(String s1, String s2) {
        return graph.containsEdge(s1, s2);
    }

    int countNeighborsIn(String s1, Collection<String> candidates) {
        return (int) candidates.stream().filter(s -> areConnected(s, s1)).count();
    }

    @Override
    public String solvePart2() {
        //YES, this would have been much easier :) but i implemented it for myself :D
        BronKerboschCliqueFinder<String, DefaultEdge> cliqueFinder = new BronKerboschCliqueFinder<>(graph);
        var it = cliqueFinder.maximumIterator();


        List<Set<String>> completeSubgraphs = new ArrayList<>();
        for (String s : new HashSet<>(graph.vertexSet())) {
            var neighbors = getNeighbors(s);
            List<String> filteredNeighbors = new ArrayList<>(neighbors);
            filteredNeighbors.sort(Comparator.comparingInt(s1 -> countNeighborsIn(s1, neighbors)));
            int maxConnections = countNeighborsIn(filteredNeighbors.getLast(), neighbors);
            var veryGoodNeighbors = filteredNeighbors.stream().filter(gn -> countNeighborsIn(gn, neighbors) == maxConnections).toList();
            if (veryGoodNeighbors.size() != maxConnections + 1) {
                continue;
            }
            var completeSubgraph = new HashSet<>(veryGoodNeighbors);
            completeSubgraph.add(s);
            completeSubgraphs.add(completeSubgraph);
        }
        completeSubgraphs.sort(Comparator.comparingInt(Set::size));
        var biggestSet = new ArrayList<>(completeSubgraphs.getLast());
        biggestSet.sort(String::compareTo);

        return StringUtil.join(biggestSet, ",");

    }
}
