package Model;

import java.util.Date;

public class FolderHeader {
    protected int id;
    protected String nameAndExtension;
    protected Date creationDate;

    public FolderHeader(int id, String nameAndExtension, Date creationDate) {
        this.id = id;
        this.nameAndExtension = nameAndExtension;
        this.creationDate = creationDate;
    }
    
    public FolderHeader(){
        
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameAndExtension() {
        return nameAndExtension;
    }

    public void setNameAndExtension(String nameAndExtension) {
        this.nameAndExtension = nameAndExtension;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    
    public String getIndicatedStringBinaryId(){
        String binary = Integer.toBinaryString(this.id);
        
        StringBuilder sb = new StringBuilder();
        sb.append(binary);
        sb.reverse();
        
        String s[] = sb.toString().split("(?<=\\G.{7})");
        String indicatedBinaryString = "";
        
        for(int i = 0 ; i < s.length; i++){
            sb.setLength(0);
            sb.append(s[i]);
            s[i] = sb.reverse().toString();
        }
        
        while(s[s.length-1].length() % 7 != 0){
            s[s.length-1] = "0" + s[s.length-1];
        }
        
        for(int i = s.length-1; i >= 0; i--){
            if(i > 0){
                indicatedBinaryString += ("1"+s[i]);
            }else{
                indicatedBinaryString += ("0"+s[i]);
            }
        }
        
        return indicatedBinaryString;
    }
    
    public void setIdFromBinaryIndicatedString(String binaryIndicatedString){
        String binaryString = "";
        
        String s[] = binaryIndicatedString.split("(?<=\\G.{8})");
        
        for(int i = 0; i < s.length; i++){
            binaryString += s[i].substring(1, 8);
        }

        this.id = Integer.parseInt(binaryString, 2);
    }
}
