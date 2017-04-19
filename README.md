## MyGit is a version control system based on Git
[![Build Status](https://travis-ci.org/Denzed/Java-4th-semester.svg?branch=VCS+)](https://travis-ci.org/Denzed/Java-4th-semester) [![codecov](https://codecov.io/gh/Denzed/Java-4th-semester/branch/VCS+/graph/badge.svg)](https://codecov.io/gh/Denzed/Java-4th-semester)
### Base objects:
- Blob --- file contents (represents a file in filesystem)
- Tree --- hierarchy of versioned files (represents a directory in filesystem)
- Commit --- root of corresponding tree, message along with the creation date and parent commit(-s)
- Branch --- link to a commit with a name

Everything is identified by its hash computed using SHA-1
### Repository structure:
- parent directory
    - .mygit
        - objects --- just a dump for objects specified above excluding Branch 
            - first 3 letters of the hash form the directory name where the object is stored 
            - the latter corresponds to its filename
        - branches --- text files named same as branches with corresponding top commit hash
        - HEAD -- currently checked out branch/commit
    - files some of which are versioned