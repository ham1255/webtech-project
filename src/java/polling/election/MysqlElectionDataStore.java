/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package polling.election;

import auth.User;
import database.MysqlProvider;
import static database.MysqlProvider.getConnection;
import database.Pageable;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 *
 * @author mohammed
 */
public class MysqlElectionDataStore implements ElectionDataStore {

    /*
    -- ELECTIONS
CREATE TABLE elections (
    election_id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    registration_starts_at DATETIME(6) NOT NULL,
    starts_at DATETIME(6) NOT NULL,
    ends_at DATETIME(6) NOT NULL
) ENGINE=InnoDB;

-- CANDIDATES
CREATE TABLE candidates (
    election_id VARCHAR(64) NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    chair_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (election_id, user_id),
    FOREIGN KEY (election_id) REFERENCES elections(election_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- VOTES (one vote per voter per election)
CREATE TABLE votes (
    election_id       VARCHAR(64) NOT NULL,
    voter_id          VARCHAR(64) NOT NULL,
    chair_name        VARCHAR(255) NOT NULL,
    candidate_user_id VARCHAR(64) NOT NULL,

    -- One vote per election + voter + chair
    PRIMARY KEY (election_id, voter_id, chair_name),

    -- Make sure candidate actually belongs to that election
    FOREIGN KEY (election_id, candidate_user_id)
        REFERENCES candidates(election_id, user_id)
        ON DELETE CASCADE,

    FOREIGN KEY (election_id) REFERENCES elections(election_id) ON DELETE CASCADE,
    FOREIGN KEY (voter_id)    REFERENCES users(id)             ON DELETE CASCADE
) ENGINE=InnoDB;

    
    
     */
    @Override
    public Election createElection(String name,
            LocalDateTime preStartsAt,
            LocalDateTime startsAt,
            LocalDateTime endsAt) {

        String electionId = UUID.randomUUID().toString().replace("-", "");

        final String sql = "INSERT INTO elections "
                + "(election_id, name, registration_starts_at, starts_at, ends_at) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, electionId);
            ps.setString(2, name);
            ps.setTimestamp(3, Timestamp.valueOf(preStartsAt));
            ps.setTimestamp(4, Timestamp.valueOf(startsAt));
            ps.setTimestamp(5, Timestamp.valueOf(endsAt));

            ps.executeUpdate();

            return new Election(electionId, name, preStartsAt, startsAt, endsAt);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to create election", e);
        }
    }

    @Override
    public void updateElection(Election election) {
        final String sql = "UPDATE elections SET "
                + "name = ?, "
                + "registration_starts_at = ?, "
                + "starts_at = ?, "
                + "ends_at = ? "
                + "WHERE election_id = ?";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, election.getName());
            ps.setTimestamp(2, Timestamp.valueOf(election.registerationStartsAt()));
            ps.setTimestamp(3, Timestamp.valueOf(election.getStartsAt()));
            ps.setTimestamp(4, Timestamp.valueOf(election.getEndsAt()));
            ps.setString(5, election.getElectionId());

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new IllegalStateException("Election not found: " + election.getElectionId());
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update election " + election.getElectionId(), e);
        }
    }

    @Override
    public void deleteElection(String id) {
        final String sql = "DELETE FROM elections WHERE election_id = ?";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, id);
            ps.executeUpdate(); // votes & candidates deleted via ON DELETE CASCADE

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete election " + id, e);
        }
    }

    @Override
    public Optional<Election> findElectionById(String id) {
        final String sql = "SELECT election_id, name, "
                + "registration_starts_at, starts_at, ends_at "
                + "FROM elections WHERE election_id = ?";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapElection(rs));
                }
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find election " + id, e);
        }
    }

    @Override
    public List<Election> findActiveElections() {
        // Active = starts_at <= NOW < ends_at
        final String sql = "SELECT election_id, name, "
                + "registration_starts_at, starts_at, ends_at "
                + "FROM elections "
                + "WHERE ends_at > NOW(6)";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            List<Election> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapElection(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find active elections", e);
        }
    }

    @Override
    public List<Election> findPastElections() {
        // Past = ends_at <= NOW
        final String sql = "SELECT election_id, name, "
                + "registration_starts_at, starts_at, ends_at "
                + "FROM elections "
                + "WHERE ends_at <= NOW(6)";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            List<Election> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapElection(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find past elections", e);
        }
    }

    @Override
    public List<Election> findAllElections() {
        final String sql = "SELECT election_id, name, "
                + "registration_starts_at, starts_at, ends_at "
                + "FROM elections";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            List<Election> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapElection(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all elections", e);
        }
    }

    @Override
    public PageableElections findActiveElectionsByPage(int page) {
        // Active: starts_at <= NOW and ends_at > NOW
        String where = "ends_at > NOW(6)";
        return queryElectionsByPage(where, page);
    }

    @Override
    public PageableElections findPastElectionsByPage(int page) {
        // Past: ends_at <= NOW
        String where = "ends_at <= NOW(6)";
        return queryElectionsByPage(where, page);
    }

    @Override
    public PageableElections findAllElectionsByPage(int page) {
        // No WHERE clause
        return queryElectionsByPage(null, page);
    }

    private PageableElections queryElectionsByPage(String extraWhere, int page) {
        if (page < 1) {
            page = 1;
        }

        final int pageSize = Pageable.MAX_PAGE_SIZE;

        String baseCountSql = "SELECT COUNT(*) FROM elections";
        String baseSelectSql = "SELECT election_id, name, "
                + "registration_starts_at, starts_at, ends_at "
                + "FROM elections";

        String whereClause = (extraWhere == null || extraWhere.isBlank())
                ? ""
                : " WHERE " + extraWhere;

        String countSql = baseCountSql + whereClause;
        String selectSql = baseSelectSql + whereClause + " ORDER BY starts_at DESC LIMIT ? OFFSET ?";

        try (Connection con = getConnection()) {

            // 1) total rows
            int total;
            try (PreparedStatement ps = con.prepareStatement(countSql); ResultSet rs = ps.executeQuery()) {
                rs.next();
                total = rs.getInt(1);
            }

            if (total == 0) {
                return new PageableElections(Collections.emptySet(), 0, page);
            }

            int maxPages = (int) Math.ceil((double) total / pageSize);
            if (page > maxPages) {
                page = maxPages;
            }

            int offset = (page - 1) * pageSize;

            Set<Election> elections = new LinkedHashSet<>();

            try (PreparedStatement ps = con.prepareStatement(selectSql)) {
                ps.setInt(1, pageSize);
                ps.setInt(2, offset);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        elections.add(mapElection(rs)); // reuse your existing mapper
                    }
                }
            }

            return new PageableElections(elections, maxPages, page);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to query elections by page", e);
        }
    }

    private Election mapElection(ResultSet rs) throws SQLException {
        String id = rs.getString("election_id");
        String name = rs.getString("name");
        LocalDateTime regStart = rs.getTimestamp("registration_starts_at").toLocalDateTime();
        LocalDateTime startsAt = rs.getTimestamp("starts_at").toLocalDateTime();
        LocalDateTime endsAt = rs.getTimestamp("ends_at").toLocalDateTime();

        return new Election(id, name, regStart, startsAt, endsAt);
    }

    @Override
    public Candidate addCandidate(Election election, Candidate candidate) {
        // We trust candidate.getElectionID() is same as election.getElectionId()
        String electionId = election.getElectionId();
        String userId = candidate.getUserID();
        StudentCouncilElectionChair chair = candidate.getChair();

        final String sql = "INSERT INTO candidates "
                + "(election_id, user_id, chair_name) "
                + "VALUES (?, ?, ?)";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, electionId);
            ps.setString(2, userId);
            ps.setString(3, chair.toString());
            ps.executeUpdate();

            // Return a new Candidate with correct electionId
            return new Candidate(userId, chair, electionId);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to add candidate " + userId
                    + " to election " + electionId, e);
        }
    }

    @Override
    public void removeCandidate(Election election, Candidate candidate) {
        final String sql = "DELETE FROM candidates WHERE election_id = ? AND user_id = ?";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, election.getElectionId());
            ps.setString(2, candidate.getUserID());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to remove candidate "
                    + candidate.getUserID() + " from election " + election.getElectionId(), e);
        }
    }

    @Override
    public List<Candidate> findCandidatesByElection(Election election) {
        final String sql = "SELECT election_id, user_id, chair_name "
                + "FROM candidates WHERE election_id = ?";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, election.getElectionId());

            try (ResultSet rs = ps.executeQuery()) {
                List<Candidate> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(mapCandidate(rs));
                }
                return list;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find candidates for election "
                    + election.getElectionId(), e);
        }
    }

    @Override
    public Optional<Candidate> findCandidateById(Election election, String candidateId) {
        // candidateId == userID of the candidate
        final String sql = "SELECT election_id, user_id, chair_name "
                + "FROM candidates WHERE election_id = ? AND user_id = ?";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, election.getElectionId());
            ps.setString(2, candidateId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapCandidate(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find candidate " + candidateId
                    + " in election " + election.getElectionId(), e);
        }
    }

    private Candidate mapCandidate(ResultSet rs) throws SQLException {
        String electionId = rs.getString("election_id");
        String userId = rs.getString("user_id");
        String chairName = rs.getString("chair_name");

        StudentCouncilElectionChair chair = StudentCouncilElectionChair.valueOf(chairName);

        return new Candidate(userId, chair, electionId);
    }

    @Override
    public void addVoteToCandidate(Election election, User voter, Candidate candidate) {
        final String sql = "INSERT INTO votes "
                + "(election_id, voter_id, chair_name, candidate_user_id) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, election.getElectionId());
            ps.setString(2, voter.id); // from your User class
            ps.setString(3, candidate.getChair().toString());
            ps.setString(4, candidate.getUserID());

            ps.executeUpdate();

        } catch (SQLIntegrityConstraintViolationException e) {
            // This means the student already voted for this chair in this election
            throw new IllegalStateException("ALREADY_VOTED_FOR_CHAIR", e);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add vote for candidate "
                    + candidate.getUserID() + " in election " + election.getElectionId(), e);
        }
    }

    @Override
    public int getVotesForCandidate(Election election, Candidate candidate) {
        final String sql = "SELECT COUNT(*) FROM votes "
                + "WHERE election_id = ? AND candidate_user_id = ?";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, election.getElectionId());
            ps.setString(2, candidate.getUserID());

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to count votes for candidate "
                    + candidate.getUserID() + " in election " + election.getElectionId(), e);
        }
    }

    @Override
    public int getTotalVotes(Election election) {
        final String sql = "SELECT COUNT(DISTINCT voter_id) FROM votes WHERE election_id = ?";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, election.getElectionId());

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to count total votes for election "
                    + election.getElectionId(), e);
        }
    }

    @Override
    public boolean hasUserVoted(Election election, User voter) {
        final String sql = "SELECT 1 FROM votes "
                + "WHERE election_id = ? AND voter_id = ? "
                + "LIMIT 1";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, election.getElectionId());
            ps.setString(2, voter.id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to check votes for user "
                    + voter.id + " in election " + election.getElectionId(), e);
        }
    }

    @Override
    public Map<StudentCouncilElectionChair, Map<String, Integer>> getCandidatesVotesPerChair(Election election) {
        final String sql
                = "SELECT chair_name, candidate_user_id, COUNT(*) AS votes "
                + "FROM votes "
                + "WHERE election_id = ? "
                + "GROUP BY chair_name, candidate_user_id";

        Map<StudentCouncilElectionChair, Map<String, Integer>> result = new LinkedHashMap<>();

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, election.getElectionId());

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    String chairName = rs.getString("chair_name");
                    String candidateUserId = rs.getString("candidate_user_id");
                    int votes = rs.getInt("votes");

                    // Convert string to the actual chair object:
                    StudentCouncilElectionChair chair = StudentCouncilElectionChair.valueOf(chairName);

                    // Ensure map exists
                    result.computeIfAbsent(chair, k -> new LinkedHashMap<>())
                            .put(candidateUserId, votes);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to get votes per chair for election " + election.getElectionId(), e
            );
        }

        return result;
    }

}
