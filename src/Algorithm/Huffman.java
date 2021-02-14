package Algorithm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Huffman extends AbstractAlgorithm {
    private static final int FREQU_CONSTANT = 48;
    private ArrayList<Node> frequencyTable;
    private ArrayList<Node> huffmanTree;
    //private String testString = "accacbcc";
    //private String testString = "accacbccddaasdddsffgghmmwwwfals";

    public Huffman(){
        System.out.println("You have chosen Huffman!");
        frequencyTable = new ArrayList<>();
        huffmanTree = new ArrayList<>();
    }

    @Override
    public byte[] compress(byte[] data) throws IOException {
        System.out.println("You have chosen to compress with Huffman!");
        createFrequencyTable(data);
        createHeader();
        createHuffmanTree();
        encode();

        ByteArrayOutputStream encodedData = new ByteArrayOutputStream();
        for(byte d: data){
            int i = 0;
            for (int j = 0; j < frequencyTable.size(); j++){
                if (d == (byte) frequencyTable.get(i).getValue()){
                    encodedData.write(frequencyTable.get(i).getCode().getBytes());
                    continue;
                }
            }
        }
        System.out.println("Encoded data: " + encodedData);
        return encodedData.toByteArray();
    }

    @Override
    public byte[] decompress(byte[] data){
        System.out.println("You have chosen to compress with Huffman!");
        //TODO
        return null;
    }

    private ArrayList<Node> createFrequencyTable(byte[] data){
        frequencyTable.clear();
        Node tempNode = new Node();

        for (int i = 0; i < data.length; i++){
            int c = data[i];
            tempNode.setValue(data[1]);
            //if (frequencyTable.contains(node with specific value){
            if (frequencyTable.stream().anyMatch(o -> o.getValue() == tempNode.getValue())){
                //increment frequency of the specific node
                frequencyTable.stream().filter(o -> o.getValue() == tempNode.getValue()).findFirst().get().incrementFrequency();
            } else {
                Node newNode = new Node();
                newNode.setValue(c);
                frequencyTable.add(newNode);
                newNode.setFrequency(1);
            }
        }
        //Order the table by frequencies
        for (int i = 1; i < frequencyTable.size(); i++){
            Node key = frequencyTable.get(i);
            int j = i - 1;
            while (j >= 0 && frequencyTable.get(j).getFrequency() > key.getFrequency()){
                Collections.swap(frequencyTable, j, j+1);
                j--;
            }
        }
        System.out.println("Node values and frequencies test:");
        for (int i = 0; i < frequencyTable.size(); i++){
            System.out.println(frequencyTable.get(i).getValue() + " - " + frequencyTable.get(i).getFrequency());
        }
        return frequencyTable;
    }

    private void createHeader() throws IOException {
        ByteArrayOutputStream header = new ByteArrayOutputStream();
        String separator = "\\||//";
        header.write(separator.getBytes());

        for (Node n: frequencyTable){
            header.write(n.nodeAttributes());
            System.out.println(header.toString());
        }
        header.write(separator.getBytes());
        System.out.println("HEADER: " + header.toString());
    }

    private void createHuffmanTree(){
        huffmanTree = new ArrayList<>(frequencyTable);
        while (huffmanTree.size() > 1){
            Node newNode = new Node();
            newNode.setFrequency(huffmanTree.get(0).getFrequency()+huffmanTree.get(1).getFrequency());
            huffmanTree.get(0).setLeftChildStatus(true);
            newNode.setLeftChild(huffmanTree.get(0));
            newNode.setRightChild(huffmanTree.get(1));
            System.out.println("1st smallest frequency: " + huffmanTree.get(0).getFrequency());
            System.out.println("2nd smallest frequency: " + huffmanTree.get(1).getFrequency());
            huffmanTree.remove(0);
            huffmanTree.remove(0);
            huffmanTree.add(newNode);

            if(huffmanTree.indexOf(newNode)>0){
                while (newNode.getFrequency() < huffmanTree.get((huffmanTree.indexOf(newNode))-1).getFrequency()){
                    Collections.swap(huffmanTree, huffmanTree.indexOf(newNode), huffmanTree.indexOf(newNode)-1);
                }
            }
            System.out.println("table size: " + huffmanTree.size());
        }
        System.out.println("final node frequency: " + huffmanTree.get(0).getFrequency());
    }

    private void encode(){
        String code = "";
        exploreNodes(huffmanTree.get(0), code);
    }

    private void exploreNodes(Node currentNode,  String code){
        if (currentNode == null){
            return;
        }

        if(currentNode.isLeftChild){
            code = code + "0";
        }

        if(!currentNode.isLeftChild){
            code = code + "1";
        }

        if (currentNode.getLeftChild() == null && currentNode.getRightChild() == null){
            currentNode.setCode(code);
            System.out.println(code);
        } else {
            exploreNodes(currentNode.getLeftChild(), code);
            exploreNodes(currentNode.getRightChild(), code);
        }
    }

    public static class Node implements Comparable<Node>{
        private int value;
        private int frequency;
        private Node leftChild;
        private Node rightChild;
        private String code;
        private boolean isLeftChild;

        Node(){
            this.frequency = 0;
            leftChild = null;
            rightChild = null;
            isLeftChild = false;
        }

        public int getFrequency() {
            return frequency;
        }

        public void setFrequency(int frequency) {
            this.frequency = frequency;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value){
            this.value = value;
        }

        public int compareTo(Node n){
            return 0;
        }

        public void incrementFrequency(){
            this.frequency++;
        }

        public Node getRightChild() {
            return rightChild;
        }

        public void setRightChild(Node rightChild) {
            this.rightChild = rightChild;
        }

        public Node getLeftChild() {
            return leftChild;
        }

        public void setLeftChild(Node leftChild) {
            this.leftChild = leftChild;
        }

        public void setCode(String code){this.code = code; }

        public String getCode(){return this.code;}

        public void setLeftChildStatus(boolean status){
            this.isLeftChild = status;
        }

        public boolean isLeftChild(){
           return  isLeftChild;
        }

        public byte[] nodeAttributes(){
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            output.write(value);
            output.write(frequency + FREQU_CONSTANT);
            return output.toByteArray();
        }
    }
}
