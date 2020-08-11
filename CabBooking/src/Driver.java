import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import java.sql.SQLIntegrityConstraintViolationException;
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;

public class Driver
{
    private String name,vname,vid;
    private float rating;
    private int numcustomer;
    private long phone;
    private Location loc;
    
    public void setStatus(String state)
    {
        String query = "UPDATE drivers SET status = ? WHERE vehicle_id = ?";
        try
        {
            Connection con = MyConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, state);
            ps.setString(2, this.getVid());
            ps.executeUpdate();
            ps.close();
            con.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
    
    public static Driver getDriver(Location p)
    {
        Driver temp = new Driver();
        Driver d = new Driver();
        double mindistance = 17;
        float maxrating = 0;
        int px = p.getX();
        int py = p.getY();
        String query = "SELECT * from drivers WHERE status = 'free'";
        try
        {
            Connection con = MyConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
            {
                do
                {
                    temp.loc = Location.getData(rs.getInt("location_no"));
                    temp.name = rs.getString("dname");
                    temp.phone = rs.getLong("phone");
                    temp.vname = rs.getString("vehicle_name");
                    temp.vid = rs.getString("vehicle_id");
                    temp.rating = rs.getFloat("rating");
                    temp.numcustomer = rs.getInt("num_rides");
                    int x = temp.loc.getX();
                    int y = temp.loc.getY();
                    double distance = (((px>x)?(px-x):(x-px)) + ((py>y)?(py-y):(y-py)))/80.0;
                    if((distance < mindistance) || (distance == mindistance && temp.rating > maxrating))
                    {
                        mindistance = distance;
                        maxrating = temp.rating;
                        d.name = temp.name;
                        d.phone = temp.phone;
                        d.vname = temp.vname;
                        d.vid = temp.vid;
                        d.rating = temp.rating;
                        d.numcustomer = temp.numcustomer;
                        d.loc = temp.loc;
                    }
                }   while(rs.next());
                d.setStatus("busy");
                rs.close();
                ps.close();
                con.close();
                return d;
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        return null;
    }
    
    public static boolean register(String fname,String lname,String vname,String vid,String phone)
    {
        if(fname.isEmpty() || vname.isEmpty() || vid.isEmpty() || phone.isEmpty())
        {
            JOptionPane.showMessageDialog(null, "Required Fields are Empty!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        else
        {
            String query = "INSERT INTO drivers (dname, phone, vehicle_name, vehicle_id, rating, num_rides, status, location_no) VALUES (?,?,?,?,5.0,0,'free',?)";
            try
            {
                long ph = Long.parseLong(phone);
                Connection con = MyConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, fname+" "+lname);
                ps.setLong(2, ph);
                ps.setString(3, vname);
                ps.setString(4, vid);
                ps.setInt(5, (int)(15*Math.random())+1);
                if(ps.executeUpdate() > 0)
                {
                    JOptionPane.showMessageDialog(null, "New Driver Added Successfully!");
                    return true;
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "Could Not Register! Try Again", "Error", JOptionPane.ERROR_MESSAGE);
                }
                ps.close();
                con.close();
            }
            catch(SQLIntegrityConstraintViolationException e)
            {
                JOptionPane.showMessageDialog(null, "Vehicle ID Already Taken!");
            }
            catch(MysqlDataTruncation e)
            {
                JOptionPane.showMessageDialog(null, "Input too long!", "Input Warning", JOptionPane.WARNING_MESSAGE);
            }
            catch(Exception e)
            {
                System.out.println(e);
                JOptionPane.showMessageDialog(null, "Server Error!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return false;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public long getPhone()
    {
        return this.phone;
    }
    
    public String getVid()
    {
        return this.vid;
    }
    
    public String getVName()
    {
        return this.vname;
    }
    
    public float getRating()
    {
        return this.rating;
    }
    
    public int getCustomer()
    {
        return this.numcustomer;
    }
    
    public Location getLocation()
    {
        return this.loc;
    }
    
    public void updateLocation(Location l)
    {
        String query = "UPDATE drivers SET location_no = ? WHERE vehicle_id = ?";
        try
        {
            Connection con = MyConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, l.getN());
            ps.setString(2, this.getVid());
            ps.executeUpdate();
            ps.close();
            con.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
    
    public void addRating(int x)
    {
        float r = ((this.getRating() * this.getCustomer()) + x)/(this.getCustomer()+1);
        try
        {
            String query = "UPDATE drivers SET num_rides = num_rides + 1 WHERE vehicle_id = ?";
            Connection con = MyConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, this.getVid());
            ps.executeUpdate();
            query = "UPDATE drivers SET rating = ? WHERE vehicle_id = ?";
            ps = con.prepareStatement(query);
            ps.setFloat(1, r);
            ps.setString(2, this.getVid());
            ps.executeUpdate();
            ps.close();
            con.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
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
