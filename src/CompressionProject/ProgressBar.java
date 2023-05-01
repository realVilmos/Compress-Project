package CompressionProject;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ProgressBar implements Runnable {
  JFrame frame = new JFrame();
  JProgressBar bar = new JProgressBar();
  JLabel progress;

  int progressLength;
  int curr;

  public ProgressBar(){
      JPanel gui = new JPanel(new GridLayout(0,1,1,1));
      gui.setBorder(new EmptyBorder(20, 20, 20, 20));
      curr = 0;

      JLabel title = new JLabel("Kérem várjon, a program dolgozik", SwingConstants.CENTER);
      gui.add(title);

      bar.setValue(0);
      bar.setStringPainted(true);
      gui.add(bar);

      progress = new JLabel("", SwingConstants.CENTER);
      gui.add(progress);

      frame.add(gui);
      frame.pack();

      frame.setTitle("Folyamatban");
      frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
      frame.setSize(620, 200);
  }

  public void setVisitble(){
    frame.setVisible(true);
    setProgressText("");
    frame.revalidate();
  }

  public void hide(){
    frame.setVisible(false);
  }

  public void setProgressLength(int num){
    this.progressLength = num;
  }
  public void increment(){
    curr++;
    double val = Math.ceil(((double)curr/(double)progressLength)*100);
    if(val > 100){
      bar.setValue(100);
    }else{
      bar.setValue((int)val);
    }
    frame.revalidate();
  }

  public void setVisible(){
    frame.setVisible(true);
  }

  public void setProgressText(String text){
    progress.setText(text);
  }

  public void reset(int length){
    setProgressLength(length);
    bar.setValue(0);
  }

  @Override
  public void run() {

  }
}
