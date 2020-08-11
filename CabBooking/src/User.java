import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;

public class User
{
    public static void setStatus(String username, String state)
    {
        String query = "UPDATE users SET status = ? WHERE username = ?";
        try
        {
            Connection con = MyConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, state);
            ps.setString(2, username);
            ps.executeUpdate();
            ps.close();
            con.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
    
    public static boolean login(String username,String password)
    {
        String query = "SELECT * FROM users WHERE username = ?";
        try
        {
            Connection con = MyConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
            {
                query = "SELECT * FROM users WHERE username = ? AND password = ?";
                ps = con.prepareStatement(query);
                ps.setString(1, username);
                ps.setString(2, password);
                rs = ps.executeQuery();
                if(rs.next())
                {
                    if(rs.getString("status").equals("free"))
                    {
                        setStatus(username, "busy");
                        BookingFrame bf = new BookingFrame(username);
                        bf.setVisible(true);
                        bf.pack();
                        return true;
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null, "Already Logged in!");
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "Wrong Password!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            else
            {
                JOptionPane.showMessageDialog(null, "User Not Found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
            rs.close();
            ps.close();
            con.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
            JOptionPane.showMessageDialog(null, "Server Error!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    
    public static boolean register(String fname,String lname,String username,String email,String pass,String repass,String phone)
    {
        if(fname.isEmpty() || username.isEmpty() || email.isEmpty() || pass.isEmpty() || phone.isEmpty())
        {
            JOptionPane.showMessageDialog(null, "Required Fields are Empty!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        else if(pass.equals(repass))
        {
            String query = "INSERT INTO users (first_name, last_name, username, email, password, phone, balance, status) VALUES (?,?,?,?,?,?,0,'free')";
            try
            {
                long ph = Long.parseLong(phone);
                Connection con = MyConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, fname);
                ps.setString(2, lname);
                ps.setString(3, username);
                ps.setString(4, email);
                ps.setString(5, pass);
                ps.setLong(6, ph);
                if(ps.executeUpdate() > 0)
                {
                    JOptionPane.showMessageDialog(null, "New User Added Successfully!");
                    return true;
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "Could Not Register! Try Again", "Error", JOptionPane.ERROR_MESSAGE);
                }
                ps.close();
                con.close();
            }
            catch(NumberFormatException e)
            {
                JOptionPane.showMessageDialog(null, "Please enter valid phone number", "Input Warning", JOptionPane.WARNING_MESSAGE);
            }
            catch(SQLIntegrityConstraintViolationException e)
            {
                JOptionPane.showMessageDialog(null, "Username Already Taken!");
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
        else
        {
            JOptionPane.showMessageDialog(null, "Passwords Do Not Match!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    
    public static void logout(String username)
    {
        setStatus(username, "free");
        LoginFrame lgf = new LoginFrame();
        lgf.setVisible(true);
        lgf.pack();
    }
    
    public static String getName(String username)
    {
        String query = "SELECT * FROM users WHERE username = ?";
        try
        {
            Connection con = MyConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
            {
                String fname = rs.getString("first_name");
                String lname = rs.getString("last_name");
                return fname+" "+lname;
            }
            rs.close();
            ps.close();
            con.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        return null;
    }
    
    public static int getBalance(String username)
    {
        String query = "SELECT * FROM users WHERE username = ?";
        try
        {
            Connection con = MyConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
            {
                return rs.getInt("balance");
            }
            rs.close();
            ps.close();
            con.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        return 0;
    }
    
    public static void updateBalance(String username, int bal)
    {
        int b = getBalance(username);
        b+=bal;
        String query = "UPDATE users SET balance = ? WHERE username = ?";
        try
        {
            Connection con = MyConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setFloat(1, b);
            ps.setString(2, username);
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
