#!/bin/bash

# Number of commits to modify (default: 5)
NUM_COMMITS=${1:-5}

# Start interactive rebase and mark all commits as "edit"
echo "Starting interactive rebase for the last $NUM_COMMITS commits..."
GIT_SEQUENCE_EDITOR="sed -i '' s/^pick/edit/'" git rebase -i HEAD~$NUM_COMMITS || exit 1

# Loop through each commit in chronological order
for commit in $(git rev-list --reverse HEAD~$NUM_COMMITS..HEAD); do
    # Get the current commit message
    MESSAGE=$(git log --format=%B -n 1 "$commit")

    echo "Current commit message:"
    echo "-----------------------------------"
    echo "$MESSAGE"
    echo "-----------------------------------"

    # Prompt the user for a new commit message (default to original if empty)
    echo "Enter a new commit message (Press Enter to keep the original):"
    read -r USER_INPUT

    if [[ -z "$USER_INPUT" ]]; then
        FORMATTED_MSG="$MESSAGE"
    else
        FORMATTED_MSG="$USER_INPUT"
    fi

    # Apply formatting to the commit message
    FORMATTED_MSG=$(echo "$FORMATTED_MSG" | awk '
        NR == 1 {
            # Capitalize first letter of subject
            sub(/^./, toupper(substr($0,1,1)) substr($0,2))
            # Remove trailing period from subject
            sub(/\.$/, "")
            # Limit subject to 72 characters
            if (length > 72) {
                $0 = substr($0, 1, 72)
            }
            print
            print ""  # Ensure blank line after subject
        }
        NR > 1 { print } # Print body as is
    ')

    echo "Final commit message:"
    echo "-----------------------------------"
    echo "$FORMATTED_MSG"
    echo "-----------------------------------"

    # Preserve original commit timestamp
    GIT_COMMITTER_DATE=$(git show -s --format=%ci "$commit")

    # Amend the commit with the formatted message
    GIT_COMMITTER_DATE="$GIT_COMMITTER_DATE" \
    git commit --amend --no-edit --date "$GIT_COMMITTER_DATE" -m "$FORMATTED_MSG" || exit 1

    # Continue rebase
    git rebase --continue || exit 1
done

# Force push the updated history
echo "Rewriting commit history, force-pushing..."
git push --force
