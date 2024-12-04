package br.univates.sistemachamados.persistencia;

public class AtendenteDao extends BaseRepository<Attendant> {
    @Override
    public void create(Attendant attendant) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            String sql = "INSERT INTO attendants (name, email, registration_number, hire_date, active) VALUES (?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, attendant.getName());
            stmt.setString(2, attendant.getEmail());
            stmt.setString(3, attendant.getRegistrationNumber());
            stmt.setDate(4, Date.valueOf(attendant.getHireDate()));
            stmt.setBoolean(5, attendant.isActive());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                attendant.setId(rs.getInt(1));
            }
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    @Override
    public void update(Attendant attendant) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            String sql = "UPDATE attendants SET name = ?, email = ?, registration_number = ?, active = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, attendant.getName());
            stmt.setString(2, attendant.getEmail());
            stmt.setString(3, attendant.getRegistrationNumber());
            stmt.setBoolean(4, attendant.isActive());
            stmt.setInt(5, attendant.getId());

            stmt.executeUpdate();
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            String sql = "DELETE FROM attendants WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    @Override
    public Attendant findById(int id) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Attendant attendant = null;
        try {
            conn = getConnection();
            String sql = "SELECT * FROM attendants WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                attendant = new Attendant();
                attendant.setId(rs.getInt("id"));
                attendant.setName(rs.getString("name"));
                attendant.setEmail(rs.getString("email"));
                attendant.setRegistrationNumber(rs.getString("registration_number"));
                attendant.setHireDate(rs.getDate("hire_date").toLocalDate());
                attendant.setActive(rs.getBoolean("active"));
            }
        } finally {
            closeResources(conn, stmt, rs);
        }
        return attendant;
    }

    @Override
    public List<Attendant> findAll() throws SQLException {
        List<Attendant> attendants = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = "SELECT * FROM attendants";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Attendant attendant = new Attendant();
                attendant.setId(rs.getInt("id"));
                attendant.setName(rs.getString("name"));
                attendant.setEmail(rs.getString("email"));
                attendant.setRegistrationNumber(rs.getString("registration_number"));
                attendant.setHireDate(rs.getDate("hire_date").toLocalDate());
                attendant.setActive(rs.getBoolean("active"));
                attendants.add(attendant);
            }
        } finally {
            closeResources(conn, stmt, rs);
        }
        return attendants;
    }
}
