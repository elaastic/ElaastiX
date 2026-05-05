# Useful knowledge
Unsorted entries of miscellaneous information that someone at some point decided was useful enough to be written here.
Think of it as the unwritten wisdom of the project, except it's actually written.

## Using generics in abstract entities
It's fine and actually pretty nice to use once you dare go head first with it. Remember to:
- Use single-letter bounds, or names that start with T (or else it might cause mapping clashes when generating diffs)
- Bound the generic type to the abstract entity
- Specify in the `@*ToOne` the target entity as the same as what you set the bound
- If IJ complains when generating diffs, tell it that it maps to `uuid` and commit jpb-settings.xml
