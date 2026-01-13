import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
/* ---------- USER CLASSES ---------- */
class User {
 String name;
 User(String name) {
 this.name = name;
 }
}
class AdvancedUser extends User {
 int age;
 AdvancedUser(String name, int age) {
 super(name);
 this.age = age;
 }
}
/* ---------- FITNESS RECORD ---------- */
class FitnessRecord {
 int steps;
double distance;
 double calories;
 LocalDate date;
 String day;
 static final int STEPS_PER_KM = 1300;
 static final double CALORIES_PER_KM = 60.0;
 FitnessRecord(Integer stepsInput, Double distanceInput) {
 // Priority to distance if both provided
 if (distanceInput != null && distanceInput > 0) {
 this.distance = distanceInput;
 this.steps = (int) (distance * STEPS_PER_KM);
 } else {
 this.steps = stepsInput;
 this.distance = steps / (double) STEPS_PER_KM;
 }
 this.calories = distance * CALORIES_PER_KM;
 this.date = LocalDate.now();
 this.day = date.getDayOfWeek().toString();
 }
 public String toString() {
 return "Date: " + date + " (" + day + ")" +
 ", Steps: " + steps +
 ", Distance: " + String.format("%.2f", distance) + " km" +
 ", Calories: " + String.format("%.2f", calories);
 }
}
/* ---------- INTERFACE ---------- */
interface TrackerThread {
 void autoSave();
}
/* ---------- MAIN GUI ---------- */
public class FitnessTracker extends Frame
 implements TrackerThread, ActionListener {
 static HashMap<String, ArrayList<FitnessRecord>> userRecords = new HashMap<>();
 static AdvancedUser user;
 TextField nameField, stepsField, distanceField;
 TextArea displayArea;
 Button addBtn, viewBtn, saveBtn;
 public static void main(String[] args) {
 new FitnessTracker();
 }
 FitnessTracker() {
 setTitle("Fitness Tracker");
setLayout(new FlowLayout());
 add(new Label("Name:"));
 nameField = new TextField(15);
 add(nameField);
 add(new Label("Steps:"));
 stepsField = new TextField(10);
 add(stepsField);
 add(new Label("Distance (km):"));
 distanceField = new TextField(10);
 add(distanceField);
 addBtn = new Button("Add Record");
 viewBtn = new Button("View Records");
 saveBtn = new Button("Save & Exit");
 add(addBtn);
 add(viewBtn);
 add(saveBtn);
 displayArea = new TextArea(12, 55);
 add(displayArea);
 addBtn.addActionListener(this);
 viewBtn.addActionListener(this);
 saveBtn.addActionListener(this);
 setSize(620, 480);
 setVisible(true);
 addWindowListener(new WindowAdapter() {
 public void windowClosing(WindowEvent e) {
 autoSave();
 dispose();
 }
 });
 }
 public void actionPerformed(ActionEvent e) {
 /* ---------- ADD RECORD ---------- */
 if (e.getSource() == addBtn) {
 try {
 String name = nameField.getText().trim();
 if (name.isEmpty()) {
 displayArea.append("Enter your name first!\n");
 return;
 }
 // Create or update user
 user = new AdvancedUser(name, 20);
 // Create list if new user
 userRecords.putIfAbsent(name, new ArrayList<>());
String stepsText = stepsField.getText().trim();
 String distText = distanceField.getText().trim();
 Integer steps = null;
 Double distance = null;
 if (!stepsText.isEmpty())
 steps = Integer.parseInt(stepsText);
 if (!distText.isEmpty())
 distance = Double.parseDouble(distText);
 if (steps == null && distance == null) {
 displayArea.append("Enter steps or distance!\n");
 return;
 }
 FitnessRecord record = new FitnessRecord(steps, distance);
 userRecords.get(name).add(record);
 displayArea.append("Record added for " + name + "!\n");
 stepsField.setText("");
 distanceField.setText("");
 } catch (Exception ex) {
 displayArea.append("Invalid Input!\n");
}
 }
 /* ---------- VIEW RECORDS ---------- */
 if (e.getSource() == viewBtn) {
 displayArea.setText("");
 String name = nameField.getText().trim();
 if (name.isEmpty() || !userRecords.containsKey(name)) {
 displayArea.append("No records found for this user.\n");
 return;
 }
 displayArea.append("User: " + name + "\n");
 displayArea.append("--------------------------------------\n");
 ArrayList<FitnessRecord> records = userRecords.get(name);
 double totalCalories = 0;
 for (FitnessRecord r : records) {
 displayArea.append(r.toString() + "\n");
 totalCalories += r.calories;
 }
 displayArea.append("--------------------------------------\n");
 displayArea.append("Total Calories Burned: " +
 String.format("%.2f", totalCalories) + "\n");
 }
* ---------- SAVE & EXIT ---------- */
 if (e.getSource() == saveBtn) {
 autoSave();
 displayArea.append("Data Saved. Closing...\n");
 dispose();
 }
 }
 /* ---------- FILE SAVE ---------- */
 public void autoSave() {
 try {
 FileWriter fw = new FileWriter("fitness_data.txt");
 for (String uname : userRecords.keySet()) {
 fw.write("User: " + uname + "\n");
 fw.write("----------------------------------\n");
 for (FitnessRecord r : userRecords.get(uname)) {
 fw.write(r.toString() + "\n");
 }
 fw.write("\n");
 }
 fw.close();
 } catch (IOException e) {
 System.out.println("File Error!");
 }
 }
}
