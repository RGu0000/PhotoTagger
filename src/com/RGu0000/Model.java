package com.RGu0000;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

class Model {

    private Map<String,TreeSet<String>> imageTags=new TreeMap<>();
    private Set<String> imagesInDirectory=new TreeSet<>(new AlphanumComparator());
    private static final String IMAGE_PATTERN = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp|jpeg))$)";
    private static final String NAME_PATTERN = "([a-zA-Z_0-9]+(\\.(?i)(jpg|png|gif|bmp|jpeg))).+";
    private static final String TAG_PATTERN = "\\+([a-zA-Z_0-9]+)";
    private File tagTXT;

    private String directoryAsString;

    List<String> getListView(){
        return new ArrayList<>(imageTags.keySet());
    }

    TreeSet<String> getTags(String fileName) {
        TreeSet<String> result=new TreeSet<>(new AlphanumComparator());
        result.addAll(imageTags.get(fileName));
        return result;
    }

    String getImageToDisplay(String imageName){
        return "file:"+directoryAsString+"/"+imageName;
    }
    String defaultImage(){ return "resources/error.jpg";}


    void scanDirectory(String dir) {
        imageTags = new HashMap<>();
        imagesInDirectory = new TreeSet<>();
        if(new File(directoryAsString).isDirectory()){
            try{
                File actual = new File(String.valueOf(dir));
                Files.list(actual.toPath())
                        .filter(n -> n.toFile().getName().matches(IMAGE_PATTERN))
                        .forEach(n -> imagesInDirectory.add(n.getFileName().toString()));

                imageTags=imagesInDirectory
                        .stream()
                        .collect(Collectors.toMap(t->t,
                                n -> new TreeSet<> () ));

            } catch (IOException | SecurityException e){
                e.getStackTrace();
            }
        }

        tagTXT = new File((String.valueOf(dir) + "\\PhotoTagsApp.txt"));
        if (tagTXT.isFile()) {
            try {
                String strLine,key;
                TreeSet<String> values;
                BufferedReader reader = new BufferedReader(new FileReader(tagTXT));
                Pattern p = Pattern.compile(TAG_PATTERN);
                Matcher m = p.matcher("");

                while ((strLine = reader.readLine()) != null) {
                    key = strLine.replaceAll(NAME_PATTERN, "$1");
                    if(imagesInDirectory.contains(key)) {
                        values = new TreeSet<>();
                        m.reset(strLine);
                        while (m.find()) {
                            values.add(m.group(1));
                        }
                        imageTags.put(key, values);
                    }
                }
            } catch (IOException | SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    void refresh(){
        scanDirectory(directoryAsString);
    }

    void chooseDirectory(){
        String checkSameDir = (directoryAsString==null)?"":directoryAsString;
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose directory");
        File directory = chooser.showDialog(new Stage());

        if(directory!=null){
            directoryAsString=directory.toString();
            if(!checkSameDir.equals(directoryAsString)) {
                scanDirectory(directoryAsString);
            }
        }
    }



    String getDirectory() {
        if(directoryAsString==null) return "";
        return directoryAsString;
    }

    void setTags(String name, TreeSet<String> tags){
        imageTags.put(name,tags);
    }

    void deleteImage(String name){
        File remove = new File((String.valueOf(directoryAsString)+"\\"+ name));

        try{
            Files.delete(remove.toPath());
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    void deleteTags(String name){
        imageTags.put(name,new TreeSet<>());
    }
    void save(){
        try (  java.io.BufferedWriter writer =
                       java.nio.file.Files.newBufferedWriter(tagTXT.toPath(), StandardCharsets.UTF_8) ) {
            StringBuilder result=new StringBuilder();

            for(Map.Entry<String,TreeSet<String>> entry:imageTags.entrySet()) {
                result.append(entry.getKey());
                result.append("+");
                result.append(entry.getValue().stream()
                        .collect(Collectors.joining("+")));
                result.append(System.getProperty("line.separator"));
            }
            writer.write(result.toString());
            Files.setAttribute(tagTXT.toPath(), "dos:hidden", true);
        } catch ( IOException e){
            e.printStackTrace();
        }
    }


    void export(String tag){
        //This lambda expression was found online:
        // https://stackoverflow.com/questions/38471056/java8-streams-transpose-map-with-values-as-list
        Map<String, List<String>> transposeMap =
                imageTags.entrySet()
                        .parallelStream()
                        .flatMap(e -> e.getValue().stream().map(i -> new AbstractMap.SimpleEntry<>(i, e.getKey())))
                        .collect(groupingBy(Map.Entry::getKey, mapping(Map.Entry::getValue, toList())));

        Path path = Paths.get(directoryAsString+"\\Grouped images");
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(tag.equals("") && !transposeMap.keySet().isEmpty()){
            DeleteDirectory.deleteDirectory(new File((directoryAsString+"\\Grouped images")));

            transposeMap.entrySet()
                    .parallelStream()
                    .forEach(entry -> {
                        try{
                            Files.createDirectories(Paths.get(directoryAsString + "\\Grouped images\\" + entry.getKey()));
                        } catch (IOException e1){
                            e1.printStackTrace();
                        }


                        entry.getValue().forEach((subvalue)-> {
                            try {
                                Files.copy(Paths.get(directoryAsString + "\\" + subvalue), Paths.get(directoryAsString + "\\Grouped images\\" + entry.getKey()+"\\"+ subvalue));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }});
                    });
        } else if(transposeMap.containsKey(tag)) {
            DeleteDirectory.deleteDirectory(new File((directoryAsString+"\\Grouped images\\"+tag)));
            try {
                Files.createDirectories(Paths.get(directoryAsString + "\\Grouped images\\" + tag));
                transposeMap.get(tag).forEach(e -> {
                    try {
                        Files.copy(Paths.get(directoryAsString + "\\" + e), Paths.get(directoryAsString + "\\Grouped images\\" + tag + "\\" + e));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                });
            } catch (IOException e1){
                e1.printStackTrace();
            }
        } else {
            DialogWindows.error();
        }

    }

    public void callDialog(){

        List<String> chooseList =
                imageTags.entrySet()
                        .parallelStream()
                        .flatMap(e -> e.getValue().stream().map(i -> new AbstractMap.SimpleEntry<>(i, e.getKey())))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
        if(!chooseList.isEmpty()) {
            String export1tag = DialogWindows.chooseTagDialog(chooseList);
            if (!export1tag.equals("")) {
                export(export1tag);
            }
        } else {
            DialogWindows.error();
        }
    }

}
