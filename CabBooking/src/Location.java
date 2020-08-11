import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;

public class Location
{
    private int x,y,n;
    private String name;
    
    public static Location getData(int m)
    {
        Location l = new Location();
        String query = "SELECT * FROM locations WHERE location_no = ?";
        try
        {
            Connection con = MyConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, m);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
            {
                l.x = rs.getInt("x");
                l.y = rs.getInt("y");
                l.n = rs.getInt("location_no");
                l.name = rs.getString("name");
            }
            rs.close();
            ps.close();
            con.close();
            return l;
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        return null;
    }
    
    public int getX()
    {
        return this.x;
    }
    
    public int getY()
    {
        return this.y;
    }
    
    public int getN()
    {
        return this.n;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BookingFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, "Please login first!", "Error", JOptionPane.ERROR_MESSAGE);
        });
    }
}
