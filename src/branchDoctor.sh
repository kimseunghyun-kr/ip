#!/usr/bin/env bash

# Number of commits to modify (default: 5)
NUM_COMMITS=${0:-5}

echo "Starting interactive rebase for the last $NUM_COMMITS commits..."

# Mark all commits as "edit" so we can amend each commit’s message
GIT_SEQUENCE_EDITOR="sed -i '' 's/^pick/edit/'" \
  git rebase -i HEAD~"$NUM_COMMITS" || exit 1

# Rebase processes from oldest to newest,
# so let's iterate over them in chronological order
for commit_hash in $(git rev-list --reverse HEAD~"$NUM_COMMITS"..HEAD); do

  # Get the current commit message
  ORIGINAL_MSG="$(git log --format=%B -n 1 "$commit_hash")"

  echo "Current commit message:"
  echo "-----------------------------------"
  echo "$ORIGINAL_MSG"
  echo "-----------------------------------"

  # Prompt for a new commit message
  echo "Enter a new commit message (Press Enter to keep the original):"
  read -r USER_INPUT

  # If user pressed Enter without typing anything, keep original
  if [[ -z "$USER_INPUT" ]]; then
    NEW_MSG="$ORIGINAL_MSG"
  else
    NEW_MSG="$USER_INPUT"
  fi

  # -- Below is a very simple “pure Bash” way to:
  #    1) Grab the first line (the "subject")
  #    2) Capitalize its first letter
  #    3) Remove any trailing period
  #    4) Put everything else into the body
  #    5) Reassemble with one blank line between subject & body

  # Split into lines
  IFS=$'\n' read -r -d '' -a LINES <<< "${NEW_MSG}"$'\n' 2>/dev/null || true

  SUBJECT="${LINES[0]}"
  BODY="${LINES[@]:1}"

  # Capitalize first character in subject
  if [[ -n "$SUBJECT" ]]; then
    first_char="${SUBJECT:0:1}"
    rest="${SUBJECT:1}"
    # Uppercase the first char
    first_char_up="$(printf "%s" "$first_char" | tr '[:lower:]' '[:upper:]')"
    SUBJECT="${first_char_up}${rest}"
    # Remove any trailing period from the subject
    SUBJECT="${SUBJECT%.}"
  fi

  # Rebuild the final message
  if [[ -n "$BODY" ]]; then
    # Join body lines with newlines, preceded by one blank line
    FINAL_MSG="$SUBJECT"$'\n\n'"$(printf "%s\n" "${BODY[@]}")"
  else
    FINAL_MSG="$SUBJECT"
  fi

  echo "Final commit message:"
  echo "-----------------------------------"
  echo "$FINAL_MSG"
  echo "-----------------------------------"

  # Keep original commit date
  GIT_COMMITTER_DATE="$(git show -s --format=%ci "$commit_hash")"

  # Amend commit with the new message
  # (No '--no-edit' because we are explicitly providing '-m')
  GIT_COMMITTER_DATE="$GIT_COMMITTER_DATE" \
    git commit --amend --date="$GIT_COMMITTER_DATE" -m "$FINAL_MSG" || exit 1

  # Move on
  git rebase --continue || exit 1

done

# Finally, force-push the updated history (optional)
echo "Rewriting commit history, now force-pushing..."
git push --force
